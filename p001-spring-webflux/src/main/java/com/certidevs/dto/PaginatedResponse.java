package com.certidevs.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> items,
        Integer page,
        Integer size,
        Long total
) {
}
