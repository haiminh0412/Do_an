package com.example.car_management.service.Interface;

import java.util.List;

public interface ICrudService<T, ID> {
    T insert(T t);
    T update(T t, ID id);
    void deleteById(ID id);
    T findById(ID id);
    List<T> findAll();
}
