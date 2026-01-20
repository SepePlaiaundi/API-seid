package com.plaiaundi.sepe.seid.dominio.dao;

import com.plaiaundi.sepe.seid.dominio.model.Bookmark;
import com.plaiaundi.sepe.seid.dominio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByUser(User user);

    Optional<Bookmark> findByUserAndCameraId(User user, String cameraId);

    void deleteByUserAndCameraId(User user, String cameraId);
}