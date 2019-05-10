package com.myself.everything.core.dao.impl;

//dao 跟数据库操作相关 -> 持久化层


import com.myself.everything.core.dao.DataSourceFactory;
import com.myself.everything.core.dao.FileIndexDao;
import com.myself.everything.core.model.Condition;
import com.myself.everything.core.model.FileType;
import com.myself.everything.core.model.Thing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//持久化层
//业务层访问数据库的增删改查的实现类
//插入、更新、删除 -> 执行更新
//查询 -> 执行查询

public class FileIndexDaoImpl implements FileIndexDao {
    private final DataSource dataSource;//数据源

    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Thing thing) {//插入
        Connection connection = null;//准备连接
        PreparedStatement statement = null;//准备插入命令
        try {
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            //可在SQL脚本中写好粘贴过来
            String sql = "insert into file_index(name, path, depth, file_type) values (?,?,?,?)";//4个问号表示参数占用(每次插入的值不同，所以先用占位符表示)
            //3.准备命令
            statement = connection.prepareStatement(sql);//预编译命令
            //4.设置参数(4个参数) 1  2  3  4    下标从 1 开始
            statement.setString(1, thing.getName());
            statement.setString(2, thing.getPath());
            statement.setInt(3, thing.getDepth());
            statement.setString(4, thing.getFileType().name());//FileType.DOC（枚举类型）->DOC（类型名称）
            //5.执行命令
            statement.executeUpdate();//执行更新（插入后要更新数据库）
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(null,statement,connection);//释放连接
        }
    }

    @Override
    public List<Thing> search(Condition condition) {//查询,跟插入不同的是，查询需要返回结果
        List<Thing> listThings = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            //String sql = "select name, path, depth, file_type from file_index";//参数占用

            //Condition
            //name       :  like   模糊匹配
            //fileType   :  =  精确匹配
            //limit      :  limit offset
            //orderByAsc :  order by 根据文件深度升序排列

            //StringBuilder线程不安全，效率高;这里不存在多线程共享（方法栈中，线程独享，在方法里边，相当于一个局部变量。
            // 如果此属性是类的属性就应该用StringBuffer），对象可能被多线程访问；StringBuffer线程安全，效率低

            //search 简历 doc/DOC

            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" select name, path, depth, file_type from file_index ");//查询时不要用*，不知道数据库有多少列
            //name匹配：前模糊、后模糊、前后模糊
            sqlBuilder.append(" where ")
                    .append(" name like '%")
                    .append(condition.getName())
                    .append("%' ");
            if (condition.getFileType() != null) {//文件类型可输入可不输入
                sqlBuilder.append(" and file_type = '")
                        .append(condition.getFileType().toUpperCase())  //防止用户输入小写的类型名
                        .append("' ");
            }
            //limit orderBy 必选
            if(condition.getOrderByAsc()!=null){
                sqlBuilder.append(" order by depth ")
                        .append(condition.getOrderByAsc() ? "asc" : "desc");
            }
            if(condition.getLimit()!=null){
                sqlBuilder.append(" limit ")
                        .append(condition.getLimit())
                        .append(" offset 0 ");
            }


            //System.out.println(sqlBuilder.toString());
            //3.准备命令
            statement = connection.prepareStatement(sqlBuilder.toString());
            //4.设置参数(4个参数)

            //5.执行命令
            resultSet = statement.executeQuery();//执行查询

            //6.处理结果
            while (resultSet.next()) {
                //数据库中的行记录变成java中的对象Thing
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                String fileType = resultSet.getString("file_type");//此时获取的是String类型的文件类型名(DOC )
                thing.setFileType(FileType.lookupByName(fileType));  //将字符串类型的文件类型名变成枚举类型的对象
                listThings.add(thing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(resultSet,statement,connection);
        }
        return listThings;

    }

    @Override
    public void delete(Thing thing) {
        Connection connection = null;//准备连接
        PreparedStatement statement = null;//准备插入命令
        try {
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            //可在SQL脚本中写好粘贴过来
            String sql = "delete from file_index where path like '"+thing.getPath()+"%'";//如果一次要删除的是一个目录，则可以一次性删除多个文件
            //3.准备命令
            statement = connection.prepareStatement(sql);//预编译命令
            //5.执行命令
            statement.executeUpdate();//执行更新（插入后要更新数据库）
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(null,statement,connection);//释放连接
        }
    }

    //解决内部代码大量重复问题：重构
    private void releaseResource(ResultSet resultSet,PreparedStatement statement,Connection connection){
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
