package com.example.giftcards.giftcards.model;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class ModelService<
        M extends ModelEntity<ID>,
        ID,
        R extends JpaRepository<M, ID>
        > {

    @Autowired
    protected R repository;

    @Transactional(readOnly = true)
    public List<M> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .toList();
    }

    @Transactional(readOnly = true)
    public M getById(ID id) {
        return getById(id, () -> {
            throw new RuntimeException(
                    "Object of class " + getModelClass() + " and id: " + id + " not found"
            );
        });
    }

    @SuppressWarnings("unchecked")
    public Class<M> getModelClass() {
        return (Class<M>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    @Transactional(readOnly = true)
    public M getById(ID id, Supplier<? extends M> supplier) {
        return repository.findById(id).orElseGet(supplier);
    }

    @Transactional
    public M save(M model) {
        return repository.save(model);
    }

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    @Transactional
    public void delete(ID id) {
        repository.deleteById(id);
    }

    @Transactional
    public void delete(M model) {
        repository.delete(model);
    }
}