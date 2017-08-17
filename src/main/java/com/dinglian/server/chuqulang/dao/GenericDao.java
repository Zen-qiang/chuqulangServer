package com.dinglian.server.chuqulang.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GenericDao <E extends Serializable> {
    public E findById(Serializable id);
    
    public E load(Serializable id);

    public void save(E e);
    
    public void saveOrUpdate(E e);

    public void delete(E e);
    
    public void update(E e);

    public List<E> query(String hql, Map<String, Object> map, Object... args);

}