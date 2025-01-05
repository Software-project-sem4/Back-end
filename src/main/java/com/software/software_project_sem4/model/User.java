package com.software.software_project_sem4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.internal.util.Lists;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends Base {

    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String userName;
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false)
    private String avatar;


    @ManyToMany
    @JoinTable(
            name = "users_liked_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> likedPosts;

    @ManyToMany
    @JoinTable(
            name = "users_liked_comments",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    private List<Comment> likedComments;

    @ManyToMany
    @JoinTable(
            name = "users_liked_replies",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "reply_id")
    )
    private List<Reply> likedReplies;

    @ManyToMany
    @JoinTable(
            name = "users_saved_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> savedPosts;
}
