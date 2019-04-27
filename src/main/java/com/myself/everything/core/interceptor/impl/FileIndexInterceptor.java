package com.myself.everything.core.interceptor.impl;

import com.myself.everything.core.common.FileConvertThing;
import com.myself.everything.core.dao.FileIndexDao;
import com.myself.everything.core.interceptor.FileInterceptor;
import com.myself.everything.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements FileInterceptor {

    private final FileIndexDao fileIndexDao;

    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing=FileConvertThing.convert(file);
        System.out.println("Thing -> "+thing);
        fileIndexDao.insert(thing);
    }
}
