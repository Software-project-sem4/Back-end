package com.software.software_project_sem4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class File extends Base {
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private byte[] fileData;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
