package com.megamanager.cliente.adapter.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response padrão para respostas paginadas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long total;
    
    public int getTotalPages() {
        return (int) Math.ceil((double) total / size);
    }
}

