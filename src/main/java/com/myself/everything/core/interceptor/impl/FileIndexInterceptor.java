package com.myself.everything.core.interceptor.impl;

import com.myself.everything.core.common.FileConvertThing;
import com.myself.everything.core.dao.FileIndexDao;
import com.myself.everything.core.interceptor.FileInterceptor;
import com.myself.everything.core.model.Thing;

import java.io.File;


//转换 + 写入
public class FileIndexInterceptor implements FileInterceptor {

    private final FileIndexDao fileIndexDao;//调用fileIndexDao的insert方法

    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing=FileConvertThing.convert(file);//转换：file -> thing
        fileIndexDao.insert(thing);
    }
}
