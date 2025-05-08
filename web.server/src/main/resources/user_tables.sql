use learning_to_hunt;

-- DDL

-- dropping these first in order to avoid foreign key errors
DROP TABLE IF EXISTS `user_roles`;
DROP TABLE IF EXISTS `user_tokens`;

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `user_id` int NOT NULL AUTO_INCREMENT,
    `email` varchar(254) NOT NULL,
    `email_confirmed` boolean NOT NULL,
    `first_name` varchar(100) NOT NULL,
    `last_name` varchar(100) NOT NULL,
    `pwd` varchar(200) NOT NULL,
    `created_at` TIMESTAMP NOT NULL,
    `created_by` varchar(50) NOT NULL,
    `updated_at` TIMESTAMP DEFAULT NULL,
    `updated_by` varchar(50) DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE `email` (`email`)
);

DROP TABLE IF EXISTS `roles`;
CREATE TABLE`roles` (
    `role_id` int NOT NULL AUTO_INCREMENT,
    `role_name` varchar(50) NOT NULL,
    `created_at` TIMESTAMP NOT NULL,
    `created_by` varchar(50) NOT NULL,
    `updated_at` TIMESTAMP DEFAULT NULL,
    `updated_by` varchar(50) DEFAULT NULL,
    PRIMARY KEY (`role_id`)
);

CREATE TABLE `user_roles` (
    `user_id` int NOT NULL,
    `role_id` int NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    PRIMARY KEY (`user_id`,`role_id`)
);

CREATE TABLE `user_tokens` (
     `token` varchar(35) NOT NULL,
     `token_timestamp` TIMESTAMP NOT NULL,
     `token_confirmed` boolean NOT NULL DEFAULT false,
     `user_id` int NOT NULL,
     `created_at` TIMESTAMP NOT NULL,
     `created_by` varchar(50) NOT NULL,
     `updated_at` TIMESTAMP DEFAULT NULL,
     `updated_by` varchar(50) DEFAULT NULL,
     FOREIGN KEY (user_id) REFERENCES users(user_id),
     PRIMARY KEY (`token`, `created_at`)
);

-- DML
REPLACE INTO `roles` (`role_id`, `role_name`,`created_at`, `created_by`)
  VALUES (1, 'ADMIN',CURDATE(),'DBA');

REPLACE INTO `roles` (`role_id`, `role_name`,`created_at`, `created_by`)
  VALUES (2, 'USER',CURDATE(),'DBA');

