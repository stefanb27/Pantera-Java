package com.example.pantera.repository.memory;


import com.example.pantera.domain.Entity;
import com.example.pantera.domain.validators.Validator;
import com.example.pantera.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return entity;
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    @Override
    public E delete(ID id) {
        E e = entities.get(id);
        entities.remove(id);
        return e;
    }

    @Override
    public E update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;

    }

    public Map<ID, E> getAll() {
        return entities;
    }

    public void show() {
        for(Map.Entry<ID, E> entry : entities.entrySet()){
            System.out.println(entry.getKey() + ": " + entry.getValue().toString());
        }
//        for(ID keys : entities.keySet()){
//            System.out.println(keys);
//        }
    }
}


