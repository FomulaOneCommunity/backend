package com.f1community.backend.domain.post;

import com.f1community.backend.dto.post.request.PostUpdateRequestDto;
import com.f1community.backend.dto.post.request.PostPatchRequestDto;
import com.f1community.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ user 엔티티와 ManyToOne 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // FK: posts.user_id → users.id
    private User user;

    // Category는 나중에 매핑
    @Column(name = "category_id")
    private Long categoryId;

    private String title;
    private String content;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(PostUpdateRequestDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.categoryId = dto.getCategoryId();
        this.updatedAt = LocalDateTime.now();
    }

    public void patch(PostPatchRequestDto dto) {
        if (dto.getTitle() != null) this.title = dto.getTitle();
        if (dto.getContent() != null) this.content = dto.getContent();
        this.updatedAt = LocalDateTime.now();
    }
}