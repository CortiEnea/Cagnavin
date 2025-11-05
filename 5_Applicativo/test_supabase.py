#!/usr/bin/env python3
"""
Script per testare la connessione a Supabase
Esegui: python test_supabase.py
"""

import os
from supabase import create_client, Client

# Carica variabili d'ambiente
SUPABASE_URL = os.environ.get('SUPABASE_URL', '')
SUPABASE_KEY = os.environ.get('SUPABASE_KEY', '')

print("=" * 50)
print("Test Connessione Supabase")
print("=" * 50)

if not SUPABASE_URL:
    print("❌ SUPABASE_URL non configurato")
    print("   Configura la variabile d'ambiente SUPABASE_URL")
    exit(1)

if not SUPABASE_KEY:
    print("❌ SUPABASE_KEY non configurato")
    print("   Configura la variabile d'ambiente SUPABASE_KEY")
    exit(1)

print(f"✅ SUPABASE_URL: {SUPABASE_URL}")
print(f"✅ SUPABASE_KEY: {SUPABASE_KEY[:20]}...")

try:
    print("\n🔌 Tentativo di connessione...")
    supabase: Client = create_client(SUPABASE_URL, SUPABASE_KEY)
    print("✅ Client Supabase creato con successo")
    
    # Test lettura tabella users
    print("\n📖 Test lettura tabella 'users'...")
    try:
        result = supabase.table('users').select('id, username').limit(1).execute()
        print(f"✅ Tabella 'users' accessibile: {len(result.data)} utenti trovati")
        if result.data:
            print(f"   Esempio: {result.data[0]}")
    except Exception as e:
        print(f"❌ Errore lettura tabella 'users': {e}")
        print("   Verifica che la tabella esista e che lo script SQL sia stato eseguito")
    
    # Test inserimento (commentato per sicurezza)
    # print("\n📝 Test inserimento...")
    # test_user = {
    #     'username': 'test_user_' + str(int(time.time())),
    #     'password': 'test_hash',
    #     'email': 'test@example.com'
    # }
    # try:
    #     result = supabase.table('users').insert(test_user).execute()
    #     print(f"✅ Inserimento test riuscito: {result.data}")
    # except Exception as e:
    #     print(f"❌ Errore inserimento: {e}")
    
    print("\n" + "=" * 50)
    print("✅ Test completato con successo!")
    print("=" * 50)
    
except Exception as e:
    print(f"\n❌ Errore connessione Supabase: {e}")
    import traceback
    traceback.print_exc()
    print("\n💡 Verifica:")
    print("   1. SUPABASE_URL è corretto?")
    print("   2. SUPABASE_KEY è la chiave 'anon public' (non service_role)?")
    print("   3. Il progetto Supabase è attivo?")
    exit(1)

