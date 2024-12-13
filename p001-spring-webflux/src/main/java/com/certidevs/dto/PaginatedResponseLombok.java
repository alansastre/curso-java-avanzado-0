package com.certidevs.dto;

import lombok.*;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class PaginatedResponseLombok<T> {
    private List<T> items;
    private Integer page;
    private Integer size;
    private Long total;
}
