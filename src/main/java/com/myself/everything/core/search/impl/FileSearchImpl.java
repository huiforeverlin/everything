package com.myself.everything.core.search.impl;

import com.myself.everything.core.dao.FileIndexDao;
import com.myself.everything.core.model.Condition;
import com.myself.everything.core.model.Thing;
import com.myself.everything.core.search.FileSearch;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


//业务层
public class FileSearchImpl implements FileSearch {
    private final FileIndexDao fileIndexDao;
    public FileSearchImpl(FileIndexDao fileIndexDao){
        this.fileIndexDao=fileIndexDao;
    }

//    //3种初始化被final修饰的变量（1.直接赋值，即立即初始化；2.构造方法初始化；3.构造块初始化{dataSource=值;}）
//    private final DataSource dataSource;
//
//    //避免耦合，如果数据源工厂发生变化，不会影响到该类的dataSource
//    public FileSearchImpl(DataSource dataSource) {//跟DataSourceFactory解耦
//        this.dataSource = dataSource;
//    }//通过构造方法完成属性dataSource的初始化操作

    @Override
    public List<Thing> search(Condition condition) {


        return this.fileIndexDao.search(condition);
    }
}
