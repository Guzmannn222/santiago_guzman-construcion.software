package com.nexusmarket.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic persistence contract for a domain entity of type {@code T} identified by {@code ID}.
 * See SDD sections 28-29 (layered architecture) and 31 (suggested implementation order).
 */
public interface Repository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);

    boolean existsById(ID id);
}
