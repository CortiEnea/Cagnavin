-- Script SQL per creare le tabelle in Supabase
-- Esegui questo script nella SQL Editor di Supabase

-- Tabella Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    profile_picture TEXT, -- Base64 encoded image
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Tabella Trips (Gite)
CREATE TABLE IF NOT EXISTS trips (
    id BIGSERIAL PRIMARY KEY,
    destinazione VARCHAR(255) NOT NULL,
    data DATE NOT NULL,
    n_min_partecipanti INTEGER DEFAULT 0,
    n_max_partecipanti INTEGER NOT NULL,
    n_partecipanti INTEGER DEFAULT 0,
    quota DECIMAL(10, 2) NOT NULL,
    descrizione TEXT,
    pranzo VARCHAR(255) DEFAULT 'Non incluso',
    url_immagine TEXT, -- Base64 encoded image
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Tabella Participants (Partecipanti)
CREATE TABLE IF NOT EXISTS participants (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    trip_id BIGINT REFERENCES trips(id) ON DELETE CASCADE,
    has_pay BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, trip_id)
);

-- Tabella Proposals (Proposte)
CREATE TABLE IF NOT EXISTS proposals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    destination VARCHAR(255) NOT NULL,
    wine_cellar_name VARCHAR(255),
    wine_cellar_address VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Tabella Requests (Richieste di partecipazione)
CREATE TABLE IF NOT EXISTS requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    trip_id BIGINT REFERENCES trips(id) ON DELETE CASCADE,
    status BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, trip_id)
);

-- Indici per migliorare le performance
CREATE INDEX IF NOT EXISTS idx_trips_data ON trips(data);
CREATE INDEX IF NOT EXISTS idx_participants_user ON participants(user_id);
CREATE INDEX IF NOT EXISTS idx_participants_trip ON participants(trip_id);
CREATE INDEX IF NOT EXISTS idx_requests_user ON requests(user_id);
CREATE INDEX IF NOT EXISTS idx_requests_trip ON requests(trip_id);
CREATE INDEX IF NOT EXISTS idx_proposals_user ON proposals(user_id);

-- Funzione per aggiornare updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger per aggiornare updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_trips_updated_at BEFORE UPDATE ON trips
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- RLS (Row Level Security) - Opzionale ma consigliato per sicurezza
-- Abilita RLS sulle tabelle
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE trips ENABLE ROW LEVEL SECURITY;
ALTER TABLE participants ENABLE ROW LEVEL SECURITY;
ALTER TABLE proposals ENABLE ROW LEVEL SECURITY;
ALTER TABLE requests ENABLE ROW LEVEL SECURITY;

-- Policy per permettere lettura pubblica delle gite
CREATE POLICY "Public trips are viewable by everyone" ON trips
    FOR SELECT USING (true);

-- Policy per permettere agli utenti autenticati di vedere i propri dati
CREATE POLICY "Users can view own profile" ON users
    FOR SELECT USING (auth.uid()::text = id::text OR username = 'Admin');

-- Policy per permettere agli utenti di inserire proposte
CREATE POLICY "Users can insert own proposals" ON proposals
    FOR INSERT WITH CHECK (true);

-- Policy per permettere agli admin di vedere tutto
CREATE POLICY "Admin can view all" ON users
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM users 
            WHERE username = 'Admin' 
            AND id::text = auth.uid()::text
        )
    );

-- Nota: Se usi Supabase Auth, sostituisci auth.uid() con la tua logica di autenticazione
-- Per ora, queste policy sono di esempio e potrebbero dover essere adattate

-- Inserisci un utente admin di default (password: admin123 - cambiala subito!)
-- La password hash qui è un esempio, usa generate_password_hash in Python per crearne una nuova
INSERT INTO users (username, password, email) 
VALUES ('Admin', 'pbkdf2:sha256:600000$...', 'admin@cagnavin.ch')
ON CONFLICT (username) DO NOTHING;

