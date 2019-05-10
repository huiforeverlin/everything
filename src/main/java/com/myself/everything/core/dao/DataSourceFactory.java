package com.myself.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.myself.everything.config.EverythingConfig;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSourceFactory {
    //数据源（单例）
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory() {

    }

    public static DataSource dataSource() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    //实例化
                    dataSource = new DruidDataSource();
                    //JDBC driver class
                    dataSource.setDriverClassName("org.h2.Driver");
                    //url,username,password
                    //采用的是h2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供url接口
                    //jdbc规范中关于MySQL：jdbc:mysql://ip:port/databaseName
                    //获取当前工程路径
//                    String workDir = System.getProperty("user.dir");
                    //JDBC规范中关于H2_jdbc:h2:filepath->存储到本地文件
                    //JDBC规范中关于H2 jdbc:h2:~/filepath->存储到当前用户的home目录
                    //JDBC规范中关于H2_jdbc:h2;//ip:port/datdabaseName->存储到服务器
                    dataSource.setUrl("jdbc:h2:" + EverythingConfig.getInstance().getH2IndexPath());
                }
            }
        }
        return dataSource;
    }

    //初始化数据库
    public static void initDatabase() {
        //1.获取数据源
        DataSource dataSource=DataSourceFactory.dataSource();
        //2.获取SQL语句
        //不采取读取文件绝对路径的方式
        //采取读取classpath路径下文件的方式
        try(InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("everything.sql");) {

            if(in==null){
                throw new RuntimeException("Not read init database script ,please check it");
            }
            StringBuilder sqlBuilder=new StringBuilder();
            try(BufferedReader reader=new BufferedReader(new InputStreamReader(in));) {
                String line;
                while ((line=reader.readLine())!=null){
                    if(!line.startsWith("--")){
                        sqlBuilder.append(line);
                    }
                }
            }
            //3.通过数据库连接和名称执行SQL
            String sql=sqlBuilder.toString();
            //JDBC
            //3.1 获取数据库连接
            Connection connection=dataSource.getConnection();
            //3.2 创建命令
            PreparedStatement statement=connection.prepareStatement(sql);
            //3.3 执行SQL语句
            statement.execute();//到这儿file_index表就创建好了
            connection.close();
            statement.close();

        }catch (IOException | SQLException e){
            e.printStackTrace();
        }


    }
}
