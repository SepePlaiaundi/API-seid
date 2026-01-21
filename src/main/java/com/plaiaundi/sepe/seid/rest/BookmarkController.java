package com.plaiaundi.sepe.seid.rest;

import com.plaiaundi.sepe.seid.dominio.services.BookmarkService;
import com.plaiaundi.sepe.seid.dto.AddBookmarkRequest;
import com.plaiaundi.sepe.seid.dto.BookmarkResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService service;

    public BookmarkController(BookmarkService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> add(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody AddBookmarkRequest request
    ) {
        service.addBookmark(principal.getUsername(), request.cameraId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cameraId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String cameraId
    ) {
        service.removeBookmark(principal.getUsername(), cameraId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BookmarkResponse>> list(
            @AuthenticationPrincipal UserDetails principal
    ) {
        List<BookmarkResponse> response =
                service.getBookmarks(principal.getUsername()).stream()
                        .map(b -> new BookmarkResponse(b.getCameraId()))
                        .toList();

        return ResponseEntity.ok(response);
    }
}
