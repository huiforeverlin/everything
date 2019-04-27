package com.myself.everything.core.index.impl;

import com.myself.everything.config.EverythingConfig;
import com.myself.everything.core.dao.DataSourceFactory;
import com.myself.everything.core.dao.impl.FileIndexDaoImpl;
import com.myself.everything.core.index.FileScan;
import com.myself.everything.core.interceptor.FileInterceptor;
import com.myself.everything.core.interceptor.impl.FileIndexInterceptor;
import com.myself.everything.core.interceptor.impl.FilePrinterInterceptor;
import com.myself.everything.core.model.Thing;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileScanImpl implements FileScan {

    //DAO
    private EverythingConfig config = EverythingConfig.getInstance();

    private LinkedList<FileInterceptor> interceptors=new LinkedList<>();

    @Override
    public void index(String path) {
        File file = new File(path);
        //List<File> fileList = new ArrayList<>();

        if (file.isFile()) {
            if (config.getExcludePath().contains(file.getParent())) {//父目录在排除文件中
                return;
            }
        } else {
            if (!config.getExcludePath().contains(path)) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        index(f.getAbsolutePath());
                    }
                }
            }
        }

        //File Directory
        for(FileInterceptor interceptor:this.interceptors){
            interceptor.apply(file);
        }

//        //文件变成thing -> 写入
//        for(File f:fileList){
//            //File -> Thing -> Dao
//
//        }

    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }



}
