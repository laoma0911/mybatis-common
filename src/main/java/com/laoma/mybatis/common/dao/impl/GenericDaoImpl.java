package com.laoma.mybatis.common.dao.impl;

import com.laoma.mybatis.common.dao.GenericDao;
import com.laoma.mybatis.common.filter.QueryFilter;
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

public abstract class GenericDaoImpl<T, PK extends Serializable> extends SqlSessionDaoSupport implements
        GenericDao<T, PK> {

    private static Log log = LogFactory.getLog(GenericDaoImpl.class);
    private String nameSpace;

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    /**
     * 获取具体的表空间名
     *
     * @return
     */
    public String getNameSpace() {
        return nameSpace;
    }

    public static final Log logger = LogFactory.getLog(GenericDaoImpl.class);

    @Override
    public int insert(T bean) {
        try {
            return getSqlSession().insert(getNameSpace() + ".insert", bean);
        } catch (Exception e) {
            log.error("insert异常", e);
            throw new RuntimeException("操作失败");
        }
    }

    @Override
    public int remove(PK key) {
        try {
            return getSqlSession().delete(getNameSpace() + ".delete", key);
        } catch (Exception e) {
            log.error("remove异常", e);
            throw new RuntimeException("操作失败");
        }
    }

    @Override
    public int batchRemove(List<PK> keyList) {
        try {
            return getSqlSession().delete(getNameSpace() + ".delete", keyList);
        } catch (Exception e) {
            log.error("batchRemove异常", e);
            throw new RuntimeException("操作失败");
        }
    }

    @Override
    public List<T> find(T bean) {
        try {
            return (List<T>) getSqlSession().selectList(getNameSpace() + ".findByBean", bean);
        } catch (Exception e) {
            log.error("find异常", e);
            throw new RuntimeException("操作失败");
        }
    }

    @Override
    public List<T> find(QueryFilter filter) {
        List<T> list = null;
        try {
            if (filter == null || filter.getPagingBean() == null) {
                list = (List<T>) getSqlSession().selectList(getNameSpace() + ".findByFilter", filter.getParameters());
            } else {
                list = (List<T>) getSqlSession().selectList(getNameSpace() + ".findByFilter", filter.getParameters(),
                        new RowBounds(filter.getPagingBean().getStart(), filter.getPagingBean().getPageSize()));
            }
        } catch (Exception e) {
            log.error("find异常", e);
            throw new RuntimeException("操作失败");
        }

        return list;
    }

    @Override
    public T get(PK key) {
        try {
            return (T) getSqlSession().selectOne(getNameSpace() + ".getByPK", key);
        } catch (Exception e) {
            log.error("get异常", e);
            throw new RuntimeException("操作失败");
        }
    }

    @Override
    public T get(T bean) {
        try {
            return (T) getSqlSession().selectOne(getNameSpace() + ".getByUniqueKey", bean);
        } catch (Exception e) {
            log.error("get异常", e);
            throw new RuntimeException("操作失败");
        }
    }

    @Override
    public int update(T bean) {
        try {
            return getSqlSession().update(getNameSpace() + ".updateByBean", bean);
        } catch (Exception e) {
            log.error("update异常", e);
            throw new RuntimeException("操作失败");
        }
    }

    @Override
    public int batchUpdate(List<T> beanList) {
        try {
            return getSqlSession().update(getNameSpace() + ".batchUpdate", beanList);
        } catch (Exception e) {
            log.error("batchUpdate异常", e);
            throw new RuntimeException("操作失败");
        }
    }

    public void batchUpdate(String statment, List<T> beanList) {
        Configuration c = this.getSqlSession().getConfiguration();

        ManagedTransactionFactory managedTransactionFactory = new ManagedTransactionFactory();
        Properties props = new Properties();
        props.setProperty("closeConnection", "true");
        managedTransactionFactory.setProperties(props);
        try {
            BatchExecutor batchExecutor = new BatchExecutor(c, managedTransactionFactory.newTransaction(this
                    .getSqlSession().getConnection()));

            int i = 0;
            int BATCH_SIZE = 200;
            for (T entity : beanList) {
                batchExecutor.doUpdate(c.getMappedStatement(getNameSpace() + "." + statment), entity);
                if (i++ > 0 && i % BATCH_SIZE == 0) {
                    batchExecutor.doFlushStatements(false);
                }
            }
            batchExecutor.doFlushStatements(false);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int count(T bean) {
        try {
            return (Integer) getSqlSession().selectOne(getNameSpace() + ".count", bean);
        } catch (Exception e) {
            log.error("count异常", e);
            throw new RuntimeException("操作失败");
        }
    }

    @Override
    public int count(QueryFilter filter) {
        try {
            return (Integer) getSqlSession().selectOne(getNameSpace() + ".countByFilter", filter.getParameters());
        } catch (Exception e) {
            log.error("count异常", e);
            throw new RuntimeException("操作失败");
        }
    }
}