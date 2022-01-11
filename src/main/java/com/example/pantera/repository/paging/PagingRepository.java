package com.example.pantera.repository.paging;


import com.example.pantera.domain.Entity;
import com.example.pantera.repository.Repository;

public interface PagingRepository<ID ,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator
}
