CREATE TABLE comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   created_by VARCHAR(255),
   created_date TIMESTAMP WITHOUT TIME ZONE,
   last_modified_by VARCHAR(255),
   last_modified_date TIMESTAMP WITHOUT TIME ZONE,
   text VARCHAR(255) NOT NULL,
   author_id BIGINT NOT NULL,
   CONSTRAINT pk_comments PRIMARY KEY (id)
);

CREATE TABLE posts_comments (
  comments_id BIGINT NOT NULL,
   post_id BIGINT NOT NULL,
   CONSTRAINT pk_posts_comments PRIMARY KEY (comments_id, post_id)
);

ALTER TABLE comments ADD CONSTRAINT FK_COMMENTS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE posts_comments ADD CONSTRAINT fk_poscom_on_comment FOREIGN KEY (comments_id) REFERENCES comments (id);

ALTER TABLE posts_comments ADD CONSTRAINT fk_poscom_on_post FOREIGN KEY (post_id) REFERENCES posts (id);