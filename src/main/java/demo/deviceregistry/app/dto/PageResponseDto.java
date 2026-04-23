package demo.deviceregistry.app.dto;

import java.util.List;

public record PageResponseDto<T>(
        List<T> data,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}

