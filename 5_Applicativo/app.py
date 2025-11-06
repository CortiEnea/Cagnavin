from flask import Flask, render_template, request, redirect, url_for, session, jsonify, send_file
from flask_cors import CORS
from werkzeug.security import check_password_hash, generate_password_hash
from werkzeug.utils import secure_filename
from datetime import datetime, date
import os
import json
from supabase import create_client, Client
import base64
from functools import wraps
from io import BytesIO

app = Flask(__name__)
app.secret_key = os.environ.get('SECRET_KEY', 'your-secret-key-change-in-production')

# --- Modifica: richiedi FRONTEND_ORIGIN esplicito (no fallback a localhost) ---
FRONTEND_ORIGIN = os.environ.get('FRONTEND_ORIGIN')  # deve essere impostato in produzione (es. https://cagnavin.vercel.app)

FLASK_ENV = os.environ.get('FLASK_ENV', 'development')
USE_SECURE_COOKIES = FLASK_ENV == 'production' or os.environ.get('SESSION_COOKIE_SECURE') == '1'

app.config['SESSION_COOKIE_SAMESITE'] = 'None'
app.config['SESSION_COOKIE_SECURE'] = bool(USE_SECURE_COOKIES)
app.config['SESSION_COOKIE_PATH'] = '/'
app.config['SESSION_COOKIE_HTTPONLY'] = True
app.config['SESSION_COOKIE_DOMAIN'] = os.environ.get('SESSION_COOKIE_DOMAIN') or None

if FRONTEND_ORIGIN:
    CORS(app, supports_credentials=True, resources={r"/*": {"origins": FRONTEND_ORIGIN}})
    print(f"✅ CORS origine frontend impostata: {FRONTEND_ORIGIN}")
else:
    # Non impostare un fallback permissivo: logga e abilita CORS in modo non-privilegiato
    CORS(app)  # evita di rispondere con Access-Control-Allow-Origin errato in produzione
    print("❌ FRONTEND_ORIGIN non impostato. In produzione imposta FRONTEND_ORIGIN=https://cagnavin.vercel.app")
# --- Fine modifica ---

# Configurazione Supabase
SUPABASE_URL = os.environ.get('SUPABASE_URL', '')
SUPABASE_KEY = os.environ.get('SUPABASE_KEY', '')

# Inizializza Supabase client
try:
    if SUPABASE_URL and SUPABASE_KEY:
        supabase: Client = create_client(SUPABASE_URL, SUPABASE_KEY)
        print(f"✅ Supabase connesso: {SUPABASE_URL}")
    else:
        supabase = None
        print("⚠️ Warning: SUPABASE_URL o SUPABASE_KEY non configurati")
except Exception as e:
    supabase = None
    print(f"❌ Errore connessione Supabase: {e}")
    print("⚠️ Assicurati di impostare SUPABASE_URL e SUPABASE_KEY")

# Configurazione upload
UPLOAD_FOLDER = 'static/images/uploads'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16MB max

# Crea la directory upload se non esiste
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def login_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user_id' not in session:
            return redirect(url_for('login'))
        return f(*args, **kwargs)
    return decorated_function

def admin_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'user_id' not in session:
            return redirect(url_for('login'))
        if session.get('username') != 'Admin':
            return redirect(url_for('home'))
        return f(*args, **kwargs)
    return decorated_function

# Routes
@app.route('/')
def home():
    try:
        return render_template('home.html', user=session.get('username'))
    except Exception as e:
        print(f"❌ Errore home: {e}")
        import traceback
        traceback.print_exc()
        return f"Errore interno: {str(e)}", 500

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form.get('username')
        password = request.form.get('password')
        
        if not username or not password:
            return render_template('login.html', error='Inserisci username e password')
        
        if not supabase:
            return render_template('login.html', error='Errore di configurazione. Contatta l\'amministratore.')
        
        try:
            result = supabase.table('users').select('*').eq('username', username).execute()
            
            if result.data and len(result.data) > 0:
                user = result.data[0]
                user_password_hash = user.get('password', '')
                
                if not user_password_hash:
                    print(f"⚠️ Utente {username} senza password hash")
                    return render_template('login.html', error='Errore nel database. Contatta l\'amministratore.')
                
                # Verifica password - check_password_hash(hash, password)
                if check_password_hash(user_password_hash, password):
                    session['user_id'] = user['id']
                    session['username'] = user['username']
                    session['email'] = user.get('email', '')
                    print(f"✅ Login riuscito: {username}")
                    return redirect(url_for('home'))
                else:
                    print(f"❌ Password errata per: {username}")
                    return render_template('login.html', error='Credenziali non valide')
            else:
                print(f"❌ Utente non trovato: {username}")
                return render_template('login.html', error='Credenziali non valide')
        except Exception as e:
            print(f"❌ Errore login: {e}")
            import traceback
            traceback.print_exc()
            return render_template('login.html', error=f'Errore durante il login: {str(e)}')
    
    return render_template('login.html')

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        username = request.form.get('username')
        password = request.form.get('password')
        confirm_password = request.form.get('confirm_password')
        email = request.form.get('email')
        
        if not all([username, password, confirm_password, email]):
            return render_template('register.html', error='Compila tutti i campi')
        
        if password != confirm_password:
            return render_template('register.html', error='Le password non coincidono')
        
        if not supabase:
            return render_template('register.html', error='Errore di configurazione. Contatta l\'amministratore.')
        
        try:
            # Verifica se username esiste già
            result = supabase.table('users').select('id').eq('username', username).execute()
            if result.data and len(result.data) > 0:
                return render_template('register.html', error='Username già in uso')
            
            # Crea nuovo utente
            hashed_password = generate_password_hash(password)
            new_user = {
                'username': username,
                'password': hashed_password,
                'email': email
            }
            
            insert_result = supabase.table('users').insert(new_user).execute()
            
            if insert_result.data:
                print(f"✅ Utente registrato: {username}")
                # TODO: Invia email di registrazione
                return redirect(url_for('login'))
            else:
                print(f"❌ Errore inserimento utente: {insert_result}")
                return render_template('register.html', error='Errore durante la registrazione')
                
        except Exception as e:
            print(f"❌ Errore registrazione: {e}")
            import traceback
            traceback.print_exc()
            error_msg = str(e)
            if 'duplicate key' in error_msg.lower() or 'unique' in error_msg.lower():
                return render_template('register.html', error='Username o email già in uso')
            return render_template('register.html', error=f'Errore durante la registrazione: {error_msg}')
    
    return render_template('register.html')

@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('home'))

@app.route('/trip')
def trip():
    filter_type = request.args.get('filter', 'all')
    user = session.get('username')
    is_admin = user == 'Admin'

    try:
        trips_result = supabase.table('trips').select('*').order('data', desc=False).execute()
        trips_raw = trips_result.data if trips_result.data else []

        today = date.today()
        trips = []
        for t in trips_raw:
            # Normalizza data come stringa ISO e crea comodi campi derivati per il template
            raw_date = t.get('data')
            try:
                if isinstance(raw_date, str):
                    d = datetime.fromisoformat(raw_date).date()
                elif hasattr(raw_date, 'isoformat'):
                    d = raw_date
                else:
                    d = None
            except Exception:
                d = None

            t['data_iso'] = d.isoformat() if d else None
            t['data_display'] = d.strftime('%d/%m/%Y') if d else 'N/A'
            t['is_future'] = bool(d and d >= today)
            trips.append(t)

        if filter_type == 'future':
            trips = [t for t in trips if t['is_future']]
        elif filter_type == 'past':
            trips = [t for t in trips if t.get('data_iso') and datetime.fromisoformat(t['data_iso']).date() < today]

        proposals = []
        if is_admin:
            proposals_result = supabase.table('proposals').select('*').execute()
            proposals = proposals_result.data if proposals_result.data else []

        return render_template('trip.html', trips=trips, proposals=proposals,
                               filter_type=filter_type, user=user, is_admin=is_admin)
    except Exception as e:
        print(f"Errore carica gite: {e}")
        import traceback
        traceback.print_exc()
        return render_template('trip.html', trips=[], proposals=[],
                               filter_type=filter_type, user=user, is_admin=is_admin)

@app.route('/add-trip', methods=['GET', 'POST'])
@admin_required
def add_trip():
    if request.method == 'POST':
        try:
            data = request.form
            image_file = request.files.get('image')
            
            image_data = None
            if image_file and allowed_file(image_file.filename):
                image_data = base64.b64encode(image_file.read()).decode('utf-8')
            
            new_trip = {
                'destinazione': data.get('destinazione'),
                'data': data.get('data'),
                'n_min_partecipanti': int(data.get('n_min_partecipanti', 0)),
                'n_max_partecipanti': int(data.get('n_max_partecipanti', 0)),
                'n_partecipanti': 0,
                'quota': float(data.get('quota', 0)),
                'descrizione': data.get('descrizione'),
                'pranzo': data.get('pranzo', 'Non incluso'),
                'url_immagine': image_data
            }
            
            supabase.table('trips').insert(new_trip).execute()
            return redirect(url_for('trip'))
        except Exception as e:
            print(f"Errore creazione gita: {e}")
            return render_template('add_trip.html', error='Errore durante la creazione della gita')
    
    return render_template('add_trip.html')

@app.route('/profile')
@login_required
def profile():
    try:
        user_result = supabase.table('users').select('*').eq('id', session['user_id']).execute()
        user = user_result.data[0] if user_result.data else None
        
        if not user:
            return redirect(url_for('login'))
        
        return render_template('profile.html', user=user)
    except Exception as e:
        print(f"Errore profilo: {e}")
        return redirect(url_for('home'))

@app.route('/request')
@admin_required
def request_view():
    try:
        requests_result = supabase.table('requests').select('*, users(*), trips(*)').execute()
        requests = requests_result.data if requests_result.data else []
        return render_template('request.html', requests=requests)
    except Exception as e:
        print(f"Errore richieste: {e}")
        return render_template('request.html', requests=[])

@app.route('/users')
@admin_required
def users_list():
    try:
        users_result = supabase.table('users').select('*').execute()
        users = users_result.data if users_result.data else []
        return render_template('users.html', users=users)
    except Exception as e:
        print(f"Errore lista utenti: {e}")
        return render_template('users.html', users=[])

# API Routes
@app.route('/api/trips', methods=['GET'])
def api_trips():
    filter_type = request.args.get('filter', 'all')
    try:
        trips_result = supabase.table('trips').select('*').order('data', desc=False).execute()
        trips = trips_result.data if trips_result.data else []
        
        today = date.today()
        for trip in trips:
            if isinstance(trip['data'], str):
                trip['data'] = datetime.fromisoformat(trip['data']).date().isoformat()
        
        if filter_type == 'future':
            trips = [t for t in trips if datetime.fromisoformat(t['data']).date() > today]
        elif filter_type == 'past':
            trips = [t for t in trips if datetime.fromisoformat(t['data']).date() < today]
        
        return jsonify(trips)
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/trips/<int:trip_id>', methods=['GET'])
def api_trip_detail(trip_id):
    try:
        result = supabase.table('trips').select('*').eq('id', trip_id).execute()
        if result.data:
            return jsonify(result.data[0])
        return jsonify({'error': 'Gita non trovata'}), 404
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/trips/<int:trip_id>', methods=['DELETE'])
@admin_required
def api_delete_trip(trip_id):
    try:
        # Elimina partecipanti e richieste associate
        supabase.table('participants').delete().eq('trip_id', trip_id).execute()
        supabase.table('requests').delete().eq('trip_id', trip_id).execute()
        supabase.table('trips').delete().eq('id', trip_id).execute()
        return jsonify({'success': True})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/requests', methods=['POST'])
@login_required
def api_create_request():
    try:
        data = request.json
        new_request = {
            'user_id': session['user_id'],
            'trip_id': data['trip_id'],
            'status': False
        }
        result = supabase.table('requests').insert(new_request).execute()
        return jsonify({'success': True, 'data': result.data[0] if result.data else None})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/requests/<int:request_id>/accept', methods=['POST'])
@admin_required
def api_accept_request(request_id):
    try:
        # Ottieni la richiesta
        request_result = supabase.table('requests').select('*, trips(*)').eq('id', request_id).execute()
        if not request_result.data:
            return jsonify({'error': 'Richiesta non trovata'}), 404
        
        req = request_result.data[0]
        trip_id = req['trip_id']
        user_id = req['user_id']
        
        # Crea partecipante
        participant = {
            'user_id': user_id,
            'trip_id': trip_id,
            'has_pay': False
        }
        supabase.table('participants').insert(participant).execute()
        
        # Aggiorna numero partecipanti
        trip_result = supabase.table('trips').select('n_partecipanti').eq('id', trip_id).execute()
        if trip_result.data:
            current = trip_result.data[0]['n_partecipanti'] or 0
            supabase.table('trips').update({'n_partecipanti': current + 1}).eq('id', trip_id).execute()
        
        # Elimina richiesta
        supabase.table('requests').delete().eq('id', request_id).execute()
        
        # TODO: Invia email con fattura
        
        return jsonify({'success': True})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/requests/<int:request_id>/reject', methods=['POST'])
@admin_required
def api_reject_request(request_id):
    try:
        supabase.table('requests').delete().eq('id', request_id).execute()
        return jsonify({'success': True})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/proposals', methods=['POST'])
@login_required
def api_create_proposal():
    try:
        data = request.json
        proposal = {
            'user_id': session['user_id'],
            'destination': data['destination'],
            'wine_cellar_name': data['wine_cellar_name'],
            'wine_cellar_address': data['wine_cellar_address']
        }
        result = supabase.table('proposals').insert(proposal).execute()
        return jsonify({'success': True, 'data': result.data[0] if result.data else None})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/proposals/<int:proposal_id>', methods=['DELETE'])
@admin_required
def api_delete_proposal(proposal_id):
    try:
        supabase.table('proposals').delete().eq('id', proposal_id).execute()
        return jsonify({'success': True})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/participants/<int:trip_id>', methods=['GET'])
def api_get_participants(trip_id):
    try:
        result = supabase.table('participants').select('*, users(*)').eq('trip_id', trip_id).execute()
        return jsonify(result.data if result.data else [])
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/users/<int:user_id>', methods=['PUT'])
@login_required
def api_update_user(user_id):
    if user_id != session['user_id'] and session.get('username') != 'Admin':
        return jsonify({'error': 'Non autorizzato'}), 403
    
    try:
        data = request.json
        update_data = {}
        
        if 'email' in data:
            update_data['email'] = data['email']
        if 'password' in data and data.get('old_password'):
            # Verifica vecchia password
            user_result = supabase.table('users').select('password').eq('id', user_id).execute()
            if user_result.data and check_password_hash(user_result.data[0]['password'], data['old_password']):
                update_data['password'] = generate_password_hash(data['password'])
            else:
                return jsonify({'error': 'Password attuale non corretta'}), 400
        
        if update_data:
            supabase.table('users').update(update_data).eq('id', user_id).execute()
        
        return jsonify({'success': True})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/image/<string:image_type>/<int:item_id>', methods=['GET'])
def api_get_image(image_type, item_id):
    try:
        if image_type == 'trip':
            result = supabase.table('trips').select('url_immagine').eq('id', item_id).execute()
            if result.data and result.data[0].get('url_immagine'):
                image_data = base64.b64decode(result.data[0]['url_immagine'])
                return send_file(BytesIO(image_data), mimetype='image/jpeg')
        elif image_type == 'user':
            result = supabase.table('users').select('profile_picture').eq('id', item_id).execute()
            if result.data and result.data[0].get('profile_picture'):
                image_data = base64.b64decode(result.data[0]['profile_picture'])
                return send_file(BytesIO(image_data), mimetype='image/jpeg')
        
        # Immagine di default
        return send_file('static/images/default-profile.jpg', mimetype='image/jpeg')
    except Exception as e:
        print(f"Errore caricamento immagine: {e}")
        try:
            return send_file('static/images/default-profile.jpg', mimetype='image/jpeg')
        except:
            return '', 404

@app.errorhandler(500)
def internal_error(error):
    import traceback
    error_details = traceback.format_exc()
    print(f"❌ Errore 500: {error_details}")
    return render_template('error.html', error="Errore interno del server"), 500

@app.errorhandler(404)
def not_found(error):
    return render_template('error.html', error="Pagina non trovata"), 404

# --- Nuova funzione: corregge host comuni con typo e reindirizza mantenendo path/query ---
COMMON_HOST_FIXES = {
    'cagnaivn': 'cagnavin',  # typo osservato: "cagnaivn" -> "cagnavin"
    # aggiungi altre varianti se necessario
}

@app.before_request
def normalize_host():
    host_port = request.host  # può essere 'host:port'
    host = host_port.split(':')[0]
    # conserva la porta se presente
    port = ''
    if ':' in host_port:
        parts = host_port.split(':', 1)
        port = ':' + parts[1]
    for bad, good in COMMON_HOST_FIXES.items():
        if host == bad or host.startswith(bad + '.'):
            corrected_host = good + port
            corrected_url = request.url.replace(request.host, corrected_host, 1)
            print(f"🔧 Host corretto: {host_port} -> {corrected_host}. Redirect a {corrected_url}")
            return redirect(corrected_url, code=302)
# --- Fine normalize_host ---

if __name__ == '__main__':
    print("=" * 50)
    print("🚀 Avvio applicazione Flask - Gruppo Cagnavin")
    print("=" * 50)
    if supabase:
        print("✅ Supabase connesso")
    else:
        print("⚠️  Supabase NON connesso - verifica le variabili d'ambiente")
    print(f"🌐 Server in ascolto su: http://0.0.0.0:{os.environ.get('PORT', 5000)}")
    print("=" * 50)
    app.run(debug=True, host='0.0.0.0', port=int(os.environ.get('PORT', 5000)))

