package com.software.software_project_sem4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
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
    private Set<File> files = new HashSet<>();

    @ManyToMany(mappedBy = "likedPosts", cascade = CascadeType.ALL)
    private Set<User> likedByUsers = new HashSet<>();

    @ManyToMany(mappedBy = "savedPosts", cascade = CascadeType.ALL)
    private Set<User> savedByUsers = new HashSet<>();

}
