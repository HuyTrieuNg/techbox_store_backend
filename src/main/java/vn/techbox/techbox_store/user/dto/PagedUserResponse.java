package vn.techbox.techbox_store.user.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedUserResponse(
    List<UserResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last,
    boolean hasNext,
    boolean hasPrevious
) {
    public static PagedUserResponse from(Page<UserResponse> page) {
        return new PagedUserResponse(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
}
