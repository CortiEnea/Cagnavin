# 🚀 Quick Start - Test Rapido

## ✅ Dipendenze Risolte!

Le dipendenze sono state aggiornate e ora funzionano. Per testare:

### 1. Configura le Variabili d'Ambiente

```bash
export SUPABASE_URL="https://tprnwtxftektlnsdqqrb.supabase.co"
export SUPABASE_KEY="la_tua_chiave_anon_public"
```

### 2. Testa la Connessione

```bash
python3 test_supabase.py
```

Dovresti vedere:
```
✅ Supabase connesso
✅ Tabella 'users' accessibile
```

### 3. Avvia l'Applicazione

```bash
python3 app.py
```

Vai su: `http://localhost:5000`

## 📝 Note

- Le dipendenze sono ora compatibili con Python 3.14
- `websockets` è stato aggiornato a 15.0.1
- `supabase` è alla versione 2.23.2
- `httpx` è alla versione 0.28.1 (compatibile)

## 🐛 Se Hai Ancora Problemi

1. **Verifica Python**: `python3 --version` (dovrebbe essere 3.14)
2. **Reinstalla dipendenze**: `pip install -r requirements.txt --upgrade`
3. **Controlla variabili**: `echo $SUPABASE_URL`

---

**Tutto pronto! 🎉**

