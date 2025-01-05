package com.software.software_project_sem4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends Base {
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<File> files = new ArrayList<>();

    @ManyToMany(mappedBy = "likedPosts", cascade = CascadeType.ALL)
    private Set<User> likedByUsers = new HashSet<>();

    @ManyToMany(mappedBy = "savedPosts", cascade = CascadeType.ALL)
    private Set<User> savedByUsers = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public int getTotalLikes() {
        return likedByUsers.size();
    }

    public int getTotalSaves() {
        return savedByUsers.size();
    }

    public boolean getLikedByCurUser(User user) {
        return likedByUsers.contains(user);
    }

    public boolean getSavedByCurUser(User user) {
        return savedByUsers.contains(user);
    }
}
