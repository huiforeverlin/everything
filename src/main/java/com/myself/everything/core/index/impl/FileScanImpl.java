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

    private LinkedList<FileInterceptor> interceptors=new LinkedList<>();//多个处理器

    @Override
    public void index(String path) {
        File file = new File(path);
        //List<File> fileList = new ArrayList<>();

        if (file.isFile()) {//是文件
            if (config.getExcludePath().contains(file.getParent())) {//父目录包含在排除目录中，则直接返回，不再遍历
                return;
            }
        } else {
            if (config.getExcludePath().contains(path)) {
                return;
            }
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    index(f.getAbsolutePath());//获取绝对路径
                }
            }
        }

        //File   遍历一个处理一个，
        for(FileInterceptor interceptor:this.interceptors){
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }
}
