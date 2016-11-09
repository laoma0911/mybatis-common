package com.laoma.mybatis.common.service.api;


import com.laoma.mybatis.common.filter.QueryFilter;

import java.io.Serializable;
import java.util.List;


public abstract interface GenericService<T, PK extends Serializable> {

    public abstract int insert(T paramT);

    public abstract int update(T paramT);

    public abstract T get(PK paramPK);

    public abstract List<T> find(QueryFilter filter);

    public abstract Integer count(QueryFilter filter);

    public abstract void remove(PK paramPK);

}