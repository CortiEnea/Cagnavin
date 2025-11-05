# Configurazione Vercel - Variabili d'Ambiente

Guida completa per configurare le variabili d'ambiente su Vercel.

## 🔑 Variabili d'Ambiente Richieste

Devi configurare **3 variabili d'ambiente** su Vercel:

### 1. SUPABASE_URL

- **Nome esatto**: `SUPABASE_URL`
- **Tipo**: Production, Preview, Development (seleziona tutte)
- **Valore**: La URL del tuo progetto Supabase
- **Formato**: `https://xxxxx.supabase.co`
- **Dove trovarla**: 
  - Supabase Dashboard → Settings → API
  - Cerca "Project URL" o "Project URL"
  - Esempio: `https://abcdefghijklmnop.supabase.co`

### 2. SUPABASE_KEY

- **Nome esatto**: `SUPABASE_KEY`
- **Tipo**: Production, Preview, Development (seleziona tutte)
- **Valore**: La chiave API pubblica (anon key) di Supabase
- **Formato**: Una stringa lunga che inizia con `eyJ...`
- **Dove trovarla**:
  - Supabase Dashboard → Settings → API
  - Cerca "anon public" o "anon public key"
  - **IMPORTANTE**: Usa la chiave **anon public**, NON la service_role key
  - Esempio: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFiY2RlZmdoaWprbG1ub3AiLCJyb2xlIjoiYW5vbiIsImlhdCI6MTYxNjIzOTAyMiwiZXhwIjoxOTMxODE1MDIyfQ.abcdefghijklmnopqrstuvwxyz1234567890`

**⚠️ FAQ - Chiave Privata (service_role) di Supabase:**
- **NON serve** per questa applicazione! 
- La **service_role key** è una chiave privata che bypassa tutte le regole di sicurezza (RLS)
- È pericolosa usarla nel frontend o in applicazioni client-side
- Per questa app Flask, usa solo la **anon public key** - è sufficiente e sicura
- La anon key rispetta le Row Level Security (RLS) policies che hai configurato in Supabase

### 3. SECRET_KEY

- **Nome esatto**: `SECRET_KEY`
- **Tipo**: Production, Preview, Development (seleziona tutte)
- **Valore**: Una chiave segreta casuale per le sessioni Flask
- **Formato**: Stringa casuale di almeno 32 caratteri
- **Come generarla**:
  ```bash
  # Su Mac/Linux:
  openssl rand -hex 32
  
  # Oppure usa questo comando Python:
  python -c "import secrets; print(secrets.token_hex(32))"
  ```
  - Esempio: `a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6`

**💡 A cosa serve SECRET_KEY?**
- **Cifra i cookie delle sessioni** degli utenti (quando fai login)
- **Protegge i dati della sessione** (username, user_id, ecc.) da modifiche esterne
- **Previene attacchi di session hijacking** (rubo della sessione)
- **Firma i cookie** in modo che Flask possa verificare che non siano stati manomessi
- **È obbligatoria** per usare `session` in Flask
- **Deve essere diversa per ogni ambiente** (non riusare la stessa chiave in produzione e sviluppo)
- **Deve essere segreta** (non committarla mai su Git pubblicamente)

## 📝 Come Configurare su Vercel

### Passo 1: Vai su Vercel Dashboard

1. Accedi a [vercel.com](https://vercel.com)
2. Seleziona il tuo progetto (o creane uno nuovo)

### Passo 2: Apri Settings

1. Clicca su **Settings** (icona ingranaggio) nella barra superiore
2. Clicca su **Environment Variables** nel menu laterale

### Passo 3: Aggiungi le Variabili

Per ogni variabile:

1. Clicca su **Add New**
2. Inserisci il **nome** (es. `SUPABASE_URL`)
3. Inserisci il **valore** (copia e incolla)
4. Seleziona gli **ambienti**:
   - ✅ **Production** (sempre)
   - ✅ **Preview** (consigliato)
   - ✅ **Development** (se usi Vercel CLI)
5. Clicca su **Save**

### Passo 4: Ripeti per Tutte le Variabili

Aggiungi tutte e 3 le variabili:
- `SUPABASE_URL`
- `SUPABASE_KEY`
- `SECRET_KEY`

### Passo 5: Ridistribuisci (se necessario)

Se hai già fatto il deploy:
1. Vai su **Deployments**
2. Clicca sui **3 punti** (...) accanto all'ultimo deploy
3. Clicca su **Redeploy**
4. Oppure fai un nuovo push su Git

## ✅ Verifica della Configurazione

Dopo aver configurato le variabili:

1. Vai su **Deployments**
2. Clicca sull'ultimo deploy
3. Controlla i **Logs** per verificare che non ci siano errori
4. Se vedi errori tipo "Invalid API key" o "SUPABASE_URL not found", ricontrolla:
   - I nomi delle variabili (devono essere esattamente come scritto sopra)
   - I valori (copia e incolla per evitare errori di digitazione)
   - Gli ambienti selezionati

## 🔒 Sicurezza

### ✅ Cosa Fare

- ✅ Usa sempre la chiave **anon public** di Supabase (non la service_role)
- ✅ Genera una SECRET_KEY casuale e lunga
- ✅ Non condividere mai le tue chiavi pubblicamente
- ✅ Usa variabili d'ambiente, non hardcoda mai le chiavi nel codice

### ❌ Cosa NON Fare

- ❌ Non committare mai il file `.env` con chiavi reali su Git
- ❌ **NON usare la service_role key di Supabase** (è troppo permissiva e pericolosa)
- ❌ **NON serve la chiave privata (service_role)** - usa solo la anon public key
- ❌ Non usare chiavi deboli per SECRET_KEY
- ❌ Non condividere screenshot con chiavi visibili
- ❌ Non riusare la stessa SECRET_KEY in produzione e sviluppo

## 🐛 Troubleshooting

### Errore: "Invalid API key"

**Causa**: La chiave SUPABASE_KEY non è corretta o hai usato la chiave sbagliata.

**Soluzione**:
1. Vai su Supabase Dashboard → Settings → API
2. Copia la chiave **anon public** (non service_role)
3. Aggiorna la variabile su Vercel
4. Ridistribuisci

### Errore: "SUPABASE_URL not found"

**Causa**: La variabile SUPABASE_URL non è configurata o il nome è sbagliato.

**Soluzione**:
1. Verifica che il nome sia esattamente `SUPABASE_URL` (maiuscole/minuscole importanti)
2. Verifica che il valore inizi con `https://` e finisca con `.supabase.co`
3. Assicurati di aver selezionato almeno "Production"
4. Ridistribuisci

### Errore: "Secret key is too short"

**Causa**: La SECRET_KEY è troppo corta o non valida.

**Soluzione**:
1. Genera una nuova chiave con `openssl rand -hex 32`
2. Assicurati che sia lunga almeno 32 caratteri
3. Aggiorna la variabile su Vercel
4. Ridistribuisci

### Le variabili non vengono applicate

**Causa**: Hai aggiunto le variabili ma non hai ridistribuito.

**Soluzione**:
1. Vai su Deployments
2. Clicca su Redeploy sull'ultimo deploy
3. Oppure fai un nuovo push su Git

## 📋 Checklist Riepilogo

Prima di fare il deploy, verifica:

- [ ] Ho configurato `SUPABASE_URL` con la URL corretta
- [ ] Ho configurato `SUPABASE_KEY` con la chiave **anon public** (NON service_role)
- [ ] Ho capito che la chiave privata (service_role) **NON serve** per questa app
- [ ] Ho configurato `SECRET_KEY` con una chiave casuale lunga (per cifrare le sessioni)
- [ ] Ho selezionato almeno "Production" per tutte le variabili
- [ ] Ho verificato che i nomi siano esattamente come scritto (case-sensitive)
- [ ] Ho fatto un redeploy o un nuovo push dopo aver configurato le variabili

## 💡 Esempio Completo

Ecco come dovrebbero apparire le variabili su Vercel:

```
Name: SUPABASE_URL
Value: https://abcdefghijklmnop.supabase.co
Environments: ✅ Production ✅ Preview ✅ Development

Name: SUPABASE_KEY
Value: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFiY2RlZmdoaWprbG1ub3AiLCJyb2xlIjoiYW5vbiIsImlhdCI6MTYxNjIzOTAyMiwiZXhwIjoxOTMxODE1MDIyfQ.abcdefghijklmnopqrstuvwxyz1234567890
Environments: ✅ Production ✅ Preview ✅ Development

Name: SECRET_KEY
Value: a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6
Environments: ✅ Production ✅ Preview ✅ Development
```

## 🔍 Differenza tra Chiavi Supabase

### Anon Public Key (quella che usiamo)
- ✅ **Pubblica** e sicura da esporre nel frontend/client
- ✅ **Rispetta le RLS policies** (Row Level Security)
- ✅ **Sicura** per applicazioni web pubbliche
- ✅ **È quella che serve** per questa applicazione Flask

### Service Role Key (NON serve)
- ❌ **Privata** e pericolosa se esposta
- ❌ **Bypassa tutte le RLS policies** (accesso completo al database)
- ❌ **Da usare solo** in ambienti server-side molto sicuri
- ❌ **NON serve** per questa applicazione

**Conclusione**: Usa solo la **anon public key**, è tutto quello che ti serve! 🎯

---

**Una volta configurate queste variabili, il tuo progetto sarà pronto per il deploy! 🚀**

