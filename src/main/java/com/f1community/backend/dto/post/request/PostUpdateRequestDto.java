package com.f1community.backend.dto.post.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequestDto {
    private Long categoryId;
    private String title;
    private String content;
}

