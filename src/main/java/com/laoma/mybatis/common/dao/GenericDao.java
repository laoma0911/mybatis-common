package com.laoma.mybatis.common.dao;


import com.laoma.mybatis.common.filter.QueryFilter;

import java.io.Serializable;
import java.util.List;


public interface GenericDao<T, PK extends Serializable> {
    /**
     * 插入
     *
     * @param bean
     * @return
     */
    public int insert(T bean);

    /**
     * 主键删除
     *
     * @param key
     * @return
     */
    public int remove(PK key);

    public int batchRemove(List<PK> keyList);

    public List<T> find(T bean);

    /**
     * 查询
     *
     * @param filter
     * @return
     */
    public List<T> find(QueryFilter filter);

    /**
     * 单个查询
     *
     * @param key
     * @return
     */
    public T get(PK key);

    /**
     * 修改
     *
     * @param bean
     * @return
     */
    public int update(T bean);

    /**
     * 查询总数
     *
     * @param filter
     * @return
     */
    public int count(QueryFilter filter);

    /**
     * 批量更新
     *
     * @param beanList
     * @return
     */
    public int batchUpdate(List<T> beanList);

    /**
     * 批量更新
     *
     * @param statment SQLID
     * @param beanList
     * @return
     */
    public void batchUpdate(String statment, List<T> beanList);

    /**
     * 查询总数
     *
     * @param bean
     * @return
     */
    public int count(T bean);

    public T get(T bean);
}
