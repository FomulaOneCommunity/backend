package com.f1community.backend.post.dto.request;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequestDto {
    private Long userId;
    private Long categoryId;
    private String title;
    private String content;
}