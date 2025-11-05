-- Script per correggere l'errore RLS (ricorsione infinita)
-- Esegui questo script in Supabase SQL Editor

-- 1. Rimuovi tutte le policy esistenti
DROP POLICY IF EXISTS "Public trips are viewable by everyone" ON trips;
DROP POLICY IF EXISTS "Users can view own profile" ON users;
DROP POLICY IF EXISTS "Users can insert own proposals" ON proposals;
DROP POLICY IF EXISTS "Admin can view all" ON users;

-- 2. Disabilita RLS (più semplice per questa applicazione Flask)
ALTER TABLE users DISABLE ROW LEVEL SECURITY;
ALTER TABLE trips DISABLE ROW LEVEL SECURITY;
ALTER TABLE participants DISABLE ROW LEVEL SECURITY;
ALTER TABLE proposals DISABLE ROW LEVEL SECURITY;
ALTER TABLE requests DISABLE ROW LEVEL SECURITY;

-- 3. Verifica che RLS sia disabilitato
SELECT tablename, rowsecurity 
FROM pg_tables 
WHERE schemaname = 'public' 
AND tablename IN ('users', 'trips', 'participants', 'proposals', 'requests');

-- Se vedi "rowsecurity = false" per tutte le tabelle, è tutto ok!

