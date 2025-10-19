package com.f1community.backend.service.post;

import com.f1community.backend.domain.post.Post;
import com.f1community.backend.dto.post.request.PostCreateRequestDto;
import com.f1community.backend.dto.post.request.PostPatchRequestDto;
import com.f1community.backend.dto.post.request.PostUpdateRequestDto;
import com.f1community.backend.dto.post.response.PostResponseDto;
import com.f1community.backend.common.exception.Post.PostNotFoundException;
import com.f1community.backend.domain.post.PostRepository;
import com.f1community.backend.domain.user.User;
import com.f1community.backend.common.exception.User.UserNotFoundException;
import com.f1community.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; 
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository; 

    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostResponseDto::fromEntity)
                .toList();
    }

    public PostResponseDto getPostById(Long id) {
        return PostResponseDto.fromEntity(postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new));
    }

    public PostResponseDto createPost(PostCreateRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        Post post = Post.builder()
                .user(user)
                .categoryId(request.getCategoryId())
                .title(request.getTitle())
                .content(request.getContent())
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return PostResponseDto.fromEntity(postRepository.save(post));
    }

    public PostResponseDto updatePost(Long id, PostUpdateRequestDto request) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
        post.update(request);
        return PostResponseDto.fromEntity(postRepository.save(post));
    }

    public PostResponseDto patchPost(Long id, PostPatchRequestDto request) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
        post.patch(request);
        return PostResponseDto.fromEntity(postRepository.save(post));
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}