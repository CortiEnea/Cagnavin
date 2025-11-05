# Gruppo Cagnavin - Applicazione Flask

Applicazione web Flask per la gestione del Gruppo Cagnavin.

## 📋 Requisiti

- **Python 3.11+**
- **Account Supabase** (vedi `SUPABASE_SETUP.md` per la configurazione)
- **Account Vercel** (per il deploy automatico)

## 🚀 Installazione

### 1. Installa le dipendenze

```bash
pip install -r requirements.txt
```

Le dipendenze includono:
- `Flask==3.0.0` - Framework web
- `flask-cors==4.0.0` - CORS per le API
- `supabase==2.3.0` - Client Supabase
- `Werkzeug==3.0.1` - Utilities Flask (password hashing)
- `python-dotenv==1.0.0` - Gestione variabili d'ambiente

### 2. Configura Supabase

Segui le istruzioni dettagliate in `SUPABASE_SETUP.md`:
- Crea un progetto Supabase
- Esegui lo script SQL in `supabase_setup.sql`
- Ottieni le credenziali API

### 3. Configura le variabili d'ambiente

Crea un file `.env` nella root del progetto (opzionale per sviluppo locale):

```env
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_KEY=eyJ...
SECRET_KEY=your-secret-key-here-change-in-production
```

**Nota**: Per produzione su Vercel, configura queste variabili nel dashboard Vercel (Settings → Environment Variables).

### 4. Copia le immagini

Assicurati di avere le immagini nella cartella `static/images/`:
- `logo.png` - Logo del gruppo
- `default-profile.jpg` - Immagine profilo di default
- `gruppone.jpg`, `gruppo.jpg`, `gruppozzo.jpg`, `gita.jpg` - Immagini per il carousel
- `vino.jpg`, `a.jpg` - Immagini per le sezioni

### 5. Avvia l'applicazione

```bash
python app.py
```

L'applicazione sarà disponibile su: `http://localhost:5000`

## ☁️ Deploy su Vercel

### Metodo 1: Via Dashboard Vercel (Online) - Consigliato

1. **Vai su [vercel.com](https://vercel.com)** e accedi

2. **Collega il repository Git**:
   - Clicca su "Add New Project"
   - Seleziona il tuo repository GitHub/GitLab
   - Vercel rileva automaticamente Flask dal file `vercel.json`

3. **Configura le variabili d'ambiente**:
   - Vai su **Settings** → **Environment Variables**
   - Aggiungi:
     - `SUPABASE_URL`: la tua URL Supabase (es. `https://xxxxx.supabase.co`)
     - `SUPABASE_KEY`: la tua chiave API pubblica Supabase (anon key)
     - `SECRET_KEY`: una chiave segreta casuale per le sessioni Flask (usa `openssl rand -hex 32` per generarla)

4. **Deploy**:
   - Clicca su "Deploy"
   - Vercel farà il deploy automaticamente
   - **Ogni push su Git farà un nuovo deploy automatico** (auto-deploy)

### Metodo 2: Auto-deploy da Git

1. **Push su Git**:
   ```bash
   git add .
   git commit -m "Initial Flask app"
   git push origin main
   ```

2. **Vercel rileva automaticamente** i push e fa il deploy

3. **Configura le variabili d'ambiente** nel dashboard Vercel se non l'hai già fatto

## 📁 Struttura Progetto

```
.
├── app.py                 # Applicazione Flask principale
├── requirements.txt       # Dipendenze Python
├── vercel.json           # Configurazione Vercel
├── supabase_setup.sql    # Script SQL per Supabase
├── SUPABASE_SETUP.md     # Guida configurazione Supabase
├── README.md             # Questo file
├── templates/            # Template HTML
│   ├── base.html         # Template base
│   ├── navbar.html       # Navbar
│   ├── footer.html       # Footer
│   ├── home.html         # Homepage
│   ├── login.html        # Login
│   ├── register.html     # Registrazione
│   ├── trip.html         # Gite
│   ├── add_trip.html     # Crea gita
│   ├── profile.html      # Profilo utente
│   ├── request.html      # Richieste
│   └── users.html        # Lista utenti
└── static/               # File statici
    ├── css/
    │   └── style.css     # Stili CSS
    └── images/           # Immagini
        └── uploads/       # Upload immagini
```

## 🎯 Funzionalità

- ✅ **Homepage** con carousel, sezioni informative e newsletter
- ✅ **Gestione Gite**: visualizzazione, creazione, eliminazione
- ✅ **Sistema di autenticazione**: login, registrazione, profilo
- ✅ **Richieste di partecipazione**: invio e gestione richieste
- ✅ **Proposte di gite**: invio e gestione proposte
- ✅ **Gestione partecipanti**: visualizzazione e gestione partecipanti alle gite
- ✅ **Area Admin**: gestione completa di gite, utenti e richieste
- ✅ **Design responsive** per mobile e desktop

## 🔧 Configurazione Database

Il database usa Supabase (PostgreSQL). Le tabelle principali sono:

- `users` - Utenti del sistema
- `trips` - Gite organizzate
- `participants` - Partecipanti alle gite
- `proposals` - Proposte di nuove gite
- `requests` - Richieste di partecipazione

Vedi `supabase_setup.sql` per la struttura completa e `SUPABASE_SETUP.md` per le istruzioni.

## 👤 Creazione Utente Admin

Dopo aver configurato Supabase, crea l'utente Admin:

1. **Opzione 1 - Via registrazione**:
   - Registrati normalmente
   - Vai su Supabase Dashboard → Table Editor → users
   - Modifica il username in "Admin"

2. **Opzione 2 - Via SQL**:
   ```sql
   -- Genera prima l'hash della password in Python:
   -- from werkzeug.security import generate_password_hash
   -- print(generate_password_hash('tua_password'))
   
   INSERT INTO users (username, password, email) 
   VALUES ('Admin', 'pbkdf2:sha256:...', 'admin@cagnavin.ch');
   ```

## 🎨 Stile

L'applicazione mantiene lo stesso stile visivo:
- Gradiente viola/bordeaux come sfondo (`#4a0e2c`, `#2c0735`)
- Colore primario: `#581c87`
- Design responsive per mobile e desktop
- Carousel immagini sulla homepage

## 📝 Note Importanti

### Immagini
- Le immagini vengono salvate come **base64** nel database Supabase
- Per produzione, considera di usare **Supabase Storage** invece di base64 per migliori performance

### Funzionalità da implementare (opzionali)
- 📧 **Email di registrazione**: invio email di benvenuto
- 📧 **Email fattura**: invio PDF fattura quando si accetta una richiesta
- 📅 **Export .ics**: download calendario con gite future
- 🗑️ **Eliminazione utente**: endpoint per eliminare utenti (Admin)

### Sicurezza
- Cambia `SECRET_KEY` in produzione (usa `openssl rand -hex 32`)
- Non committare mai il file `.env` con credenziali reali
- Usa variabili d'ambiente per tutte le configurazioni sensibili

## 🐛 Troubleshooting

### Errore: "Invalid API key"
- Verifica di aver copiato correttamente la chiave API Supabase
- Controlla che non ci siano spazi extra
- Assicurati di usare la **anon public key**, non la service role key

### Errore: "relation does not exist"
- Esegui lo script SQL in `supabase_setup.sql`
- Verifica che tutte le tabelle siano create nella Table Editor

### Errore: "permission denied"
- Controlla le RLS (Row Level Security) policies in Supabase
- Potresti dover disabilitare temporaneamente RLS per testare

### Immagini non caricate
- Verifica che la cartella `static/images/` contenga le immagini necessarie
- Controlla i permessi della cartella `uploads/`
- Verifica che le immagini siano nel formato corretto (JPG, PNG, GIF)

### Problemi con Vercel
- Verifica che tutte le variabili d'ambiente siano configurate
- Controlla i log di deploy in Vercel
- Assicurati che `vercel.json` sia presente nella root

## 📞 Supporto

Per problemi:
- **Supabase**: [Documentazione](https://supabase.com/docs) | [Discord](https://discord.supabase.com)
- **Vercel**: [Documentazione](https://vercel.com/docs) | [Discord](https://vercel.com/discord)
- **Flask**: [Documentazione](https://flask.palletsprojects.com/)

## 📄 Licenza

Vedi file LICENSE.md se presente nel repository.

---

**Buon deploy! 🚀**
