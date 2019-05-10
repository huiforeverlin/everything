package com.myself.everything.core.index;

import com.myself.everything.core.dao.DataSourceFactory;
import com.myself.everything.core.dao.impl.FileIndexDaoImpl;
import com.myself.everything.core.index.impl.FileScanImpl;
import com.myself.everything.core.interceptor.FileInterceptor;
import com.myself.everything.core.interceptor.impl.FileIndexInterceptor;
import com.myself.everything.core.interceptor.impl.FilePrinterInterceptor;


//扫描遍历文件
public interface FileScan {
    void index(String path);//（插入），遍历path

    void interceptor(FileInterceptor interceptor);//添加处理器（拦截器）
}
