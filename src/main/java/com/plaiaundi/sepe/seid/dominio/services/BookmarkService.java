package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.BookmarkRepository;
import com.plaiaundi.sepe.seid.dominio.dao.UserRepository;
import com.plaiaundi.sepe.seid.dominio.model.Bookmark;
import com.plaiaundi.sepe.seid.dominio.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository,
                           UserRepository userRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addBookmark(String email, String cameraId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        bookmarkRepository.save(new Bookmark(user, cameraId));
    }

    @Transactional
    public void removeBookmark(String email, String cameraId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        bookmarkRepository.deleteByUserAndCameraId(user, cameraId);
    }

    @Transactional(readOnly = true)
    public List<Bookmark> getBookmarks(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return bookmarkRepository.findByUser(user);
    }
}
