# 🔧 Risoluzione Errore Python 3.14

## ❌ Problema

Stai usando **Python 3.14**, che è troppo nuovo e non è ancora supportato da tutte le librerie (in particolare `httpx` e `httpcore` usate da Supabase).

L'errore che vedi è:
```
AttributeError: 'typing.Union' object has no attribute '__module__'
```

## ✅ Soluzione

### Opzione 1: Usa Python 3.11 o 3.12 (Consigliato)

**Vercel supporta Python 3.11**, quindi è la scelta migliore.

#### Su Mac (con Homebrew):

```bash
# Installa Python 3.11
brew install python@3.11

# Crea un ambiente virtuale con Python 3.11
python3.11 -m venv venv

# Attiva l'ambiente virtuale
source venv/bin/activate

# Installa le dipendenze
pip install --upgrade pip
pip install -r requirements.txt

# Verifica la versione
python --version  # Dovrebbe mostrare Python 3.11.x
```

#### Su Mac (con pyenv):

```bash
# Installa pyenv se non ce l'hai
brew install pyenv

# Installa Python 3.11
pyenv install 3.11.9

# Imposta Python 3.11 per questo progetto
cd /Users/eneacorti/Cagnavin/5_Applicativo
pyenv local 3.11.9

# Crea ambiente virtuale
python -m venv venv
source venv/bin/activate

# Installa dipendenze
pip install --upgrade pip
pip install -r requirements.txt
```

### Opzione 2: Aggiorna le Dipendenze (Tentativo)

Se vuoi continuare con Python 3.14, prova ad aggiornare le dipendenze:

```bash
# Disinstalla le versioni problematiche
pip uninstall httpx httpcore supabase -y

# Installa versioni più recenti
pip install --upgrade httpx httpcore supabase

# Reinstalla tutto
pip install -r requirements.txt
```

**Nota**: Questo potrebbe non funzionare se Python 3.14 non è ancora supportato dalle librerie.

## 🚀 Dopo aver Risolto

1. **Testa la connessione**:
   ```bash
   python test_supabase.py
   ```

2. **Avvia l'applicazione**:
   ```bash
   python app.py
   ```

3. **Verifica che funzioni**:
   - Vai su http://localhost:5000
   - Prova a registrarti
   - Prova a fare login

## 📝 Per Vercel

Vercel usa Python 3.11 di default, quindi il deploy funzionerà correttamente. Il problema è solo locale con Python 3.14.

## ✅ Verifica

Dopo aver cambiato versione Python, verifica:

```bash
python --version  # Dovrebbe essere 3.11.x o 3.12.x
python test_supabase.py  # Dovrebbe funzionare senza errori
```

## 🆘 Se Continui ad Avere Problemi

1. **Usa un ambiente virtuale**:
   ```bash
   python3.11 -m venv venv
   source venv/bin/activate
   pip install -r requirements.txt
   ```

2. **Verifica le versioni installate**:
   ```bash
   pip list | grep -E "(supabase|httpx|httpcore)"
   ```

3. **Reinstalla tutto**:
   ```bash
   pip uninstall -y -r requirements.txt
   pip install -r requirements.txt
   ```

---

**Raccomandazione**: Usa Python 3.11 per questo progetto, è la versione più stabile e supportata.

