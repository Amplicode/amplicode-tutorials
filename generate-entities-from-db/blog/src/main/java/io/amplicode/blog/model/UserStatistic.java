package io.amplicode.blog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/**
 * Mapping for DB view
 */
@Entity
@Immutable
@Table(name = "user_statistics")
public class UserStatistic {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "number_of_posts")
    private Long numberOfPosts;

    public Long getUserId() {
        return userId;
    }

    public Long getNumberOfPosts() {
        return numberOfPosts;
    }

    protected UserStatistic() {
    }
}