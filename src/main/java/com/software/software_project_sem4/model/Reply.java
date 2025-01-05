package com.software.software_project_sem4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reply extends Base {
    @ManyToMany(mappedBy = "likedReplies", cascade = CascadeType.ALL)
    private List<User> likedByUsers = new ArrayList<>();

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Reply> replies = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "reply_id", nullable = false)
    private Reply reply;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
