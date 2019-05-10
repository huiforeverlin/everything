package com.myself.everything.core.dao;

import com.myself.everything.core.model.Condition;
import com.myself.everything.core.model.Thing;

import java.util.List;


//关于业务层访问数据库的CRUD（增删改查）
public interface FileIndexDao {

    //插入数据Thing
    void insert(Thing thing);

    //根据condition条件进行数据库的检索、查询
    List<Thing> search(Condition condition);

    //删除数据Thing
    void delete(Thing thing);
}
