-- Create points table for time tracking
CREATE TABLE points (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    timestamp TIMESTAMP NOT NULL,
    type VARCHAR(50) NOT NULL
);

-- Add index on user_id for faster queries
CREATE INDEX idx_points_user_id ON points(user_id);

-- Add index on timestamp for faster time-based queries
CREATE INDEX idx_points_timestamp ON points(timestamp);
