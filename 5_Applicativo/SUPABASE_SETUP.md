# Configurazione Supabase per Cagnavin

## 1. Crea un progetto Supabase

1. Vai su [https://supabase.com](https://supabase.com)
2. Clicca su "Start your project" o "Sign in"
3. Clicca su "New Project"
4. Inserisci:
   - **Project Name**: cagnavin (o un nome a tua scelta)
   - **Database Password**: scegli una password sicura (salvala!)
   - **Region**: scegli la regione più vicina (es. Europe West)
5. Clicca su "Create new project"
6. Attendi che il progetto sia pronto (circa 2 minuti)

## 2. Esegui lo script SQL

1. Nel dashboard Supabase, vai su **SQL Editor** (icona nella sidebar sinistra)
2. Clicca su **New Query**
3. Copia e incolla il contenuto del file `supabase_setup.sql`
4. Clicca su **Run** (o premi Ctrl+Enter)
5. Verifica che tutte le tabelle siano state create correttamente:
   - Vai su **Table Editor** nella sidebar
   - Dovresti vedere: `users`, `trips`, `participants`, `proposals`, `requests`

## 3. Ottieni le credenziali API

1. Nel dashboard Supabase, vai su **Settings** (icona ingranaggio in basso a sinistra)
2. Vai su **API**
3. Trova:
   - **Project URL**: copia questo valore (es. `https://xxxxx.supabase.co`)
   - **anon public key**: copia questo valore (inizia con `eyJ...`)

## 4. Configura le variabili d'ambiente su Vercel

### Metodo 1: Via Dashboard Vercel (Online)

1. Vai su [https://vercel.com](https://vercel.com) e accedi
2. Vai al tuo progetto (o creane uno nuovo)
3. Vai su **Settings** → **Environment Variables**
4. Aggiungi le seguenti variabili:

   ```
   SUPABASE_URL = https://xxxxx.supabase.co
   SUPABASE_KEY = eyJ... (la tua anon public key)
   SECRET_KEY = una-chiave-segreta-casuale-per-le-sessioni
   ```

5. Clicca su **Save**

### Metodo 2: Via File .env (Locale)

Crea un file `.env` nella root del progetto (NON committarlo su Git):

```
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_KEY=eyJ...
SECRET_KEY=your-secret-key-here
```

## 5. Testa la connessione

1. Avvia l'applicazione localmente:
   ```bash
   pip install -r requirements.txt
   python app.py
   ```

2. Verifica che non ci siano errori di connessione nella console

## 6. Crea l'utente Admin

1. Vai su **Table Editor** → users**
2. Clicca su **Insert row**
3. Inserisci:
   - `username`: Admin
   - `password`: (usa Python per generare l'hash)
   - `email`: admin@cagnavin.ch

Per generare la password hash, esegui:
```python
from werkzeug.security import generate_password_hash
print(generate_password_hash('tua_password_admin'))
```

Oppure usa la prima registrazione per creare l'utente Admin e poi modifica manualmente il username in "Admin" dalla Table Editor.

## 7. Storage per immagini (Opzionale)

Se vuoi usare Supabase Storage invece di base64:

1. Vai su **Storage** nel dashboard Supabase
2. Crea un bucket chiamato `images`
3. Configura le policy per permettere upload/lettura
4. Modifica il codice in `app.py` per usare Supabase Storage invece di base64

## Note importanti

- **Sicurezza**: Non condividere mai le tue chiavi API pubblicamente
- **RLS**: Le Row Level Security policies potrebbero dover essere adattate in base alle tue esigenze
- **Backup**: Supabase fa backup automatici, ma è sempre bene avere un backup locale
- **Limiti**: Controlla i limiti del piano gratuito di Supabase

## Troubleshooting

### Errore: "Invalid API key"
- Verifica di aver copiato correttamente la `anon public key`
- Assicurati di non aver incluso spazi extra

### Errore: "relation does not exist"
- Verifica di aver eseguito correttamente lo script SQL
- Controlla che tutte le tabelle siano presenti nella Table Editor

### Errore: "permission denied"
- Controlla le RLS policies
- Potresti dover disabilitare temporaneamente RLS per testare

## Supporto

Per problemi con Supabase:
- [Documentazione Supabase](https://supabase.com/docs)
- [Discord Community](https://discord.supabase.com)

