use learning_to_hunt;

ALTER TABLE articles
    ADD `created_at` TIMESTAMP NOT NULL,
    ADD `created_by` varchar(50) NOT NULL,
    ADD `updated_at` TIMESTAMP DEFAULT NULL,
    ADD `updated_by` varchar(50) DEFAULT NULL;

UPDATE articles set
                    created_at = create_date,
                    created_by = 'rich@argohaus.com',
                    updated_at = modify_date,
                    updated_by = 'rich@argohaus.com';

ALTER TABLE articles
DROP COLUMN if exists `create_date`,
    DROP COLUMN if exists `modify_date`;

