package com.myself.everything.core.search;

import com.alibaba.druid.util.DaemonThreadFactory;
import com.myself.everything.core.dao.DataSourceFactory;
import com.myself.everything.core.dao.impl.FileIndexDaoImpl;
import com.myself.everything.core.model.Condition;
import com.myself.everything.core.model.Thing;
import com.myself.everything.core.search.impl.FileSearchImpl;

import java.util.List;



public interface FileSearch {
    //根据condition条件进行数据库的检索，返回Thing类实例化对象的属性
    List<Thing> search(Condition condition);

    public static void main(String[] args) {
        FileSearch fileSearch=new FileSearchImpl(new FileIndexDaoImpl(DataSourceFactory.dataSource()));
        List<Thing> list=fileSearch.search(new Condition());
        System.out.println(list);
    }
}
