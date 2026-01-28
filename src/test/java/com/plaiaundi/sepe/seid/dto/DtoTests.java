package com.plaiaundi.sepe.seid.dto;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class DtoTests {

    @Test
    void testAddBookmarkRequest() {
        AddBookmarkRequest request = new AddBookmarkRequest("CAM123");
        assertEquals("CAM123", request.cameraId());
    }

    @Test
    void testBookmarkResponse() {
        BookmarkResponse response = new BookmarkResponse("CAM123");
        assertEquals("CAM123", response.cameraId());
    }

    @Test
    void testLoginResponse() {
        LoginResponse response = new LoginResponse("token123");
        assertEquals("token123", response.getToken());
    }

    @Test
    void testUserLoginRequest() {
        UserLoginRequest request = new UserLoginRequest("test@test.com", "pass123");
        assertEquals("test@test.com", request.email());
        assertEquals("pass123", request.password());
    }

    @Test
    void testUserRegisterRequest() {
        UserRegisterRequest request = new UserRegisterRequest("John Doe", "test@test.com", "pass123");
        assertEquals("John Doe", request.nombreCompleto());
        assertEquals("test@test.com", request.email());
        assertEquals("pass123", request.password());
    }

    @Test
    void testOpenDataCameraResponse() {
        OpenDataCameraResponse response = new OpenDataCameraResponse(10, 1, 0, Collections.emptyList());
        assertEquals(1, response.totalPages());
        assertEquals(10, response.totalItems());
        assertTrue(response.cameras().isEmpty());
    }

    @Test
    void testOpenDataSourceResponse() {
        OpenDataSourceResponse response = new OpenDataSourceResponse(new ArrayList<>());
        assertNotNull(response.recursos());
    }
}
