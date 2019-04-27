package com.myself.everything.core.dao;

import com.myself.everything.core.model.Condition;
import com.myself.everything.core.model.Thing;

import java.util.List;


//业务层访问数据库的CRUD
public interface FileIndexDao {
    //插入
    void insert(Thing thing);
    //查询
    List<Thing> search(Condition condition);
}
