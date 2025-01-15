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
public class Reply extends Base {

    @Column(nullable = false)
    private String replyContent;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(mappedBy = "likedReplies", cascade = CascadeType.ALL)
    private List<User> likedByUsers = new ArrayList<>();

    // Parent Reply (self-referencing relationship)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reply_id", nullable = true) // Allow null for top-level replies
    private Reply parentReply;

    // Child Replies (self-referencing relationship)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reply> childReplies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}



