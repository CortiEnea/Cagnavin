# Guida Debug - Risoluzione Errori

## 🔍 Come Diagnosticare gli Errori

### 1. Testa la Connessione Supabase

Prima di tutto, verifica che Supabase sia configurato correttamente:

```bash
# Configura le variabili d'ambiente
export SUPABASE_URL="https://xxxxx.supabase.co"
export SUPABASE_KEY="eyJ..."

# Esegui il test
python test_supabase.py
```

**Se il test fallisce:**
- Verifica che SUPABASE_URL e SUPABASE_KEY siano corretti
- Controlla che il progetto Supabase sia attivo
- Verifica che le tabelle siano state create (vedi `supabase_setup.sql`)

### 2. Controlla i Log dell'Applicazione

Quando avvii l'applicazione, controlla la console per messaggi come:

```
✅ Supabase connesso: https://xxxxx.supabase.co
✅ Login riuscito: username
```

**Se vedi errori:**
- `❌ Errore connessione Supabase` → Verifica SUPABASE_URL e SUPABASE_KEY
- `⚠️ Warning: SUPABASE_URL o SUPABASE_KEY non configurati` → Configura le variabili d'ambiente
- `❌ Errore login: ...` → Vedi il messaggio di errore completo

### 3. Errori Comuni e Soluzioni

#### Errore: "Internal Server Error"

**Possibili cause:**
1. **Supabase non configurato**
   - Verifica che SUPABASE_URL e SUPABASE_KEY siano impostati
   - Controlla i log all'avvio dell'app

2. **Tabella non esiste**
   - Esegui lo script `supabase_setup.sql` in Supabase
   - Verifica che tutte le tabelle siano create

3. **Errore nelle query**
   - Controlla i log della console per l'errore completo
   - Verifica che i nomi delle colonne siano corretti

#### Login non funziona

**Possibili cause:**
1. **Password hash non corrisponde**
   - Se hai creato utenti manualmente, usa `generate_password_hash` per l'hash
   - Verifica che la password sia stata hashata correttamente

2. **Utente non esiste**
   - Verifica che l'utente esista nella tabella `users`
   - Controlla che lo username sia corretto (case-sensitive)

3. **Errore query Supabase**
   - Controlla i log per vedere l'errore esatto
   - Verifica che la tabella `users` esista

#### Registrazione non funziona

**Possibili cause:**
1. **Username già esistente**
   - Il messaggio di errore dovrebbe indicarlo
   - Prova con un username diverso

2. **Errore inserimento**
   - Controlla i log per l'errore completo
   - Verifica che tutti i campi richiesti siano presenti nella tabella

3. **Vincolo UNIQUE violato**
   - Username o email già esistenti
   - Prova con valori diversi

### 4. Verifica Database Supabase

1. Vai su **Supabase Dashboard** → **Table Editor**
2. Verifica che esistano queste tabelle:
   - `users`
   - `trips`
   - `participants`
   - `proposals`
   - `requests`

3. Controlla la struttura della tabella `users`:
   - Deve avere: `id`, `username`, `password`, `email`
   - `username` deve essere UNIQUE
   - `password` deve essere VARCHAR abbastanza lungo (almeno 255)

### 5. Test Manuale

#### Test Login
```python
from werkzeug.security import generate_password_hash, check_password_hash

# Crea un hash di password
password = "miapassword"
hash = generate_password_hash(password)
print(f"Hash: {hash}")

# Verifica
print(check_password_hash(hash, password))  # True
print(check_password_hash(hash, "password_sbagliata"))  # False
```

#### Test Query Supabase
```python
from supabase import create_client
import os

supabase = create_client(
    os.environ.get('SUPABASE_URL'),
    os.environ.get('SUPABASE_KEY')
)

# Test lettura
result = supabase.table('users').select('*').limit(1).execute()
print(result.data)
```

### 6. Debug in Produzione (Vercel)

1. Vai su **Vercel Dashboard** → **Deployments**
2. Clicca sull'ultimo deploy
3. Vai su **Logs** per vedere gli errori
4. Cerca messaggi che iniziano con `❌` o `⚠️`

### 7. Verifica Variabili d'Ambiente

**Locale:**
```bash
echo $SUPABASE_URL
echo $SUPABASE_KEY
echo $SECRET_KEY
```

**Vercel:**
- Vai su Settings → Environment Variables
- Verifica che tutte le variabili siano presenti
- Controlla che i valori siano corretti (senza spazi extra)

### 8. Checklist Debug

- [ ] Supabase è configurato? (SUPABASE_URL e SUPABASE_KEY)
- [ ] Le tabelle sono state create? (verifica in Supabase Dashboard)
- [ ] Le variabili d'ambiente sono corrette?
- [ ] I log mostrano errori specifici?
- [ ] Il test `test_supabase.py` funziona?
- [ ] La password è stata hashata correttamente?
- [ ] I nomi delle colonne nel database corrispondono al codice?

## 🆘 Se Nulla Funziona

1. **Riesegui lo script SQL** in Supabase
2. **Rigenera le variabili d'ambiente** (soprattutto SECRET_KEY)
3. **Controlla i log completi** nella console
4. **Verifica la versione di Python** (deve essere 3.11+)
5. **Reinstalla le dipendenze**: `pip install -r requirements.txt --upgrade`

## 📞 Supporto

Se continui ad avere problemi, fornisci:
- Messaggio di errore completo (dai log)
- Output di `python test_supabase.py`
- Screenshot delle variabili d'ambiente (senza mostrare i valori!)
- Versione di Python: `python --version`

