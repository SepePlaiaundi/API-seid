package com.plaiaundi.sepe.seid.dominio.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "camera_id"})
        }
)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "camera_id", nullable = false)
    private String cameraId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    protected Bookmark() {
    }

    public Bookmark(User user, String cameraId) {
        this.user = user;
        this.cameraId = cameraId;
    }

    public Long getId() {
        return id;
    }

    public String getCameraId() {
        return cameraId;
    }

    public User getUser() {
        return user;
    }
}
