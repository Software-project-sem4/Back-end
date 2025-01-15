package com.software.software_project_sem4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends Base {

    @Column(nullable = false)
    private String commentContent;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(mappedBy = "likedComments", cascade = CascadeType.ALL)
    private List<User> likedByUsers = new ArrayList<>(); // Users who liked this comment

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reply> replies = new ArrayList<>(); // Replies to this comment

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // The post this comment belongs to

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who created this comment
}

