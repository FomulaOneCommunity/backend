package com.f1community.backend.post.controller;

import com.f1community.backend.post.dto.request.PostCreateRequestDto;
import com.f1community.backend.post.dto.request.PostPatchRequestDto;
import com.f1community.backend.post.dto.request.PostUpdateRequestDto;
import com.f1community.backend.post.service.PostService;
import com.f1community.backend.post.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Posts", description = "Posts-related API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @Operation(
            summary = "Get all posts",
            description = "Retrieves all posts."
    )
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @Operation(
            summary = "Get a specific post",
            description = "Retrieves the post with the given post ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @Operation(
            summary = "Create a new post",
            description = "Creates a new post."
    )
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostCreateRequestDto request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @Operation(
            summary = "Update a post",
            description = "Updates the post with the given post ID."
    )
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestBody PostUpdateRequestDto request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    @Operation(
            summary = "Partially update a post",
            description = "Partially updates the post with the given post ID."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<PostResponseDto> patchPost(@PathVariable Long id, @RequestBody PostPatchRequestDto request) {
        return ResponseEntity.ok(postService.patchPost(id, request));
    }

    @Operation(
            summary = "Delete a post",
            description = "Deletes the post with the given post ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}