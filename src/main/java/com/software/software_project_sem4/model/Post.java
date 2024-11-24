package com.software.software_project_sem4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends Base {
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany
    private Set<File> files;

    @ManyToMany(mappedBy = "likedPosts")
    private Set<User> likedByUsers;

    @ManyToMany(mappedBy = "savedPosts")
    private Set<User> savedByUsers;

}
