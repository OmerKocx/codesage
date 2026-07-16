CREATE TABLE analysis (
    id UUID PRIMARY KEY,
    repo_url VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL,
    overall_score INT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE source_file (
    id UUID PRIMARY KEY,
    analysis_id UUID NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    package_name VARCHAR(255),
    class_name VARCHAR(255),
    CONSTRAINT fk_source_file_analysis FOREIGN KEY (analysis_id) REFERENCES analysis(id) ON DELETE CASCADE
);

CREATE TABLE issue (
    id UUID PRIMARY KEY,
    analysis_id UUID NOT NULL,
    source_file_id UUID,
    severity VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    line_number INT,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    suggestion TEXT,
    CONSTRAINT fk_issue_analysis FOREIGN KEY (analysis_id) REFERENCES analysis(id) ON DELETE CASCADE,
    CONSTRAINT fk_issue_source_file FOREIGN KEY (source_file_id) REFERENCES source_file(id) ON DELETE SET NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);