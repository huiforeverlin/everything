package com.myself.everything.core.interceptor;

import java.io.File;

//文件处理器（文件拦截器）：打印、转换 + 写入

@FunctionalInterface   //函数接口
public interface FileInterceptor {
    int count=0;
    void apply(File file);
}
