package com.f1community.backend.post.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPatchRequestDto {
    private String title;
    private String content;
}
