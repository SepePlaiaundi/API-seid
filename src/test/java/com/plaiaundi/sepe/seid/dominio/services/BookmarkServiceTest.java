package com.plaiaundi.sepe.seid.dominio.services;

import com.plaiaundi.sepe.seid.dominio.dao.BookmarkRepository;
import com.plaiaundi.sepe.seid.dominio.dao.UserRepository;
import com.plaiaundi.sepe.seid.dominio.model.Bookmark;
import com.plaiaundi.sepe.seid.dominio.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddBookmark() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        bookmarkService.addBookmark("test@example.com", "CAM123");

        // Then
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void shouldRemoveBookmark() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        bookmarkService.removeBookmark("test@example.com", "CAM123");

        // Then
        verify(bookmarkRepository).deleteByUserAndCameraId(user, "CAM123");
    }

    @Test
    void shouldGetBookmarks() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bookmarkRepository.findByUser(user)).thenReturn(List.of(new Bookmark(user, "CAM123")));

        // When
        List<Bookmark> result = bookmarkService.getBookmarks("test@example.com");

        // Then
        assertEquals(1, result.size());
        assertEquals("CAM123", result.get(0).getCameraId());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookmarkService.getBookmarks("none@example.com"));
    }
}
