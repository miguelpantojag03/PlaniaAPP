CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    total_points INTEGER NOT NULL DEFAULT 0,
    current_streak INTEGER NOT NULL DEFAULT 0,
    last_activity_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    color VARCHAR(7) NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_categories_name_user UNIQUE (name, user_id)
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000),
    due_date DATE NOT NULL,
    due_time TIME,
    priority VARCHAR(20) NOT NULL,
    energy_required VARCHAR(20) NOT NULL,
    estimated_minutes INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    postponed_count INTEGER NOT NULL DEFAULT 0,
    smart_score INTEGER NOT NULL DEFAULT 0,
    category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT chk_tasks_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    CONSTRAINT chk_tasks_energy CHECK (energy_required IN ('LOW', 'MEDIUM', 'HIGH')),
    CONSTRAINT chk_tasks_status CHECK (status IN ('PENDING', 'COMPLETED', 'POSTPONED', 'CANCELLED')),
    CONSTRAINT chk_tasks_estimated_minutes CHECK (estimated_minutes BETWEEN 5 AND 1440)
);

CREATE TABLE IF NOT EXISTS moods (
    id BIGSERIAL PRIMARY KEY,
    mood_type VARCHAR(30) NOT NULL,
    note VARCHAR(500),
    date DATE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_moods_date_user UNIQUE (date, user_id),
    CONSTRAINT chk_moods_type CHECK (mood_type IN ('ENERGETIC', 'NORMAL', 'TIRED', 'STRESSED', 'UNMOTIVATED'))
);

CREATE TABLE IF NOT EXISTS subtasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS achievements (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    points_required INTEGER NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_categories_user_id ON categories(user_id);
CREATE INDEX IF NOT EXISTS idx_moods_user_id_date ON moods(user_id, date);
CREATE INDEX IF NOT EXISTS idx_subtasks_task_id ON subtasks(task_id);
