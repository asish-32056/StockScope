package com.example.stockscope.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T, ID> {
    T findById(ID id);
    Page<T> findAll(Pageable pageable);
    T save(T entity);
    void deleteById(ID id);
    boolean existsById(ID id);
}