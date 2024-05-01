CREATE TABLE resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE resource_skills (
    resource_id BIGINT NOT NULL,
    skill VARCHAR(255),
    PRIMARY KEY (resource_id, skill),
    FOREIGN KEY (resource_id) REFERENCES resources(id)
);
