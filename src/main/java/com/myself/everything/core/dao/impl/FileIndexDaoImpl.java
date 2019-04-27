package com.myself.everything.core.dao.impl;

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

public class FileIndexDaoImpl implements FileIndexDao {
    private final DataSource dataSource;

    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            String sql = "insert into file_index (name, path, depth, file_type) VALUES (?,?,?,?)";//参数占用
            //3.准备命令
            statement = connection.prepareStatement(sql);
            //4.设置参数(4个参数)
            statement.setString(1, thing.getName());
            statement.setString(2, thing.getPath());
            statement.setInt(3, thing.getDepth());
            statement.setString(4, thing.getFileType().name());//FileType.DOC->DOC
            //5.执行命令
            statement.executeUpdate();
        } catch (SQLException e) {

        } finally {
            releaseResource(null,statement,connection);
        }
    }

    @Override
    public List<Thing> search(Condition condition) {
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
            //name: like
            //fileType: =
            //limit: limit offset
            //根据文件深度升序排列
            //orderByAsc :order by

            //StringBuilder线程不安全，效率高;这里不存在多线程共享（在方法里边，相当于一个局部变量。如果此属性是类的属性就应该用StringBuffer）；StringBuffer线程安全，效率低
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" select name, path, depth, file_type from file_index ");
            //name匹配：前模糊、后模糊、前后模糊
            sqlBuilder.append(" where ")
                    .append(" name like '%")
                    .append(condition.getName())
                    .append("%' ");
            if (condition.getFileType() != null) {
                sqlBuilder.append(" and file_Type = '")
                        .append(condition.getFileType().toUpperCase())
                        .append("' ");//防止用户输入小写的类型名
            }
            //limit orderBy 必选
            sqlBuilder.append(" order by depth ")
                    .append(condition.getOrderByAsc() ? "asc" : "desc")
                    .append(" limit ")
                    .append(condition.getLimit())
                    .append(" offset 0 ");


            System.out.println(sqlBuilder.toString());
            //3.准备命令
            statement = connection.prepareStatement(sqlBuilder.toString());
            //4.设置参数(4个参数)

            //5.执行命令
            resultSet = statement.executeQuery();

            //6.处理结果
            while (resultSet.next()) {
                //数据库中的行记录变成java中的对象Thing
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                String fileType = resultSet.getString("file_type");
                thing.setFileType(FileType.lookupByName(fileType));
                listThings.add(thing);
            }
        } catch (SQLException e) {

        } finally {
            releaseResource(resultSet,statement,connection);
        }
        return listThings;

    }

    //解决内部代码大量重复问题：：重构
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

//    public static void main(String[] args) {
//        FileIndexDao fileIndexDao = new FileIndexDaoImpl(DataSourceFactory.dataSource());
//        Thing thing = new Thing();
//        thing.setName("第一版简历.docx");
//        thing.setPath("C:\\Users\\meili\\Desktop\\简历\\第零版\\第一版简历.docx");
//        thing.setDepth(6);
//        thing.setFileType(FileType.DOC);
//        //fileIndexDao.insert(thing);
//
//        Condition condition=new Condition();
//        condition.setName("简历");
//        condition.setLimit(2);
//        condition.setOrderByAsc(true);
//
//        List<Thing> things = fileIndexDao.search(condition);
//        //System.out.println(things.size());
//        for (Thing t : things) {
//            System.out.println(t);
//        }
//    }
}
