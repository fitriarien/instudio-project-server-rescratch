package com.fitriarien.instudio.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateResponse<T> {

    private T data;

    private String errors;

    private PagingResponse paging;

}
