package com.myself.everything.core.interceptor.impl;

import com.myself.everything.core.interceptor.FileInterceptor;

import java.io.File;

public class FilePrinterInterceptor implements FileInterceptor {
    @Override
    public void apply(File file) {
        System.out.println(file.getAbsolutePath());
    }
}
