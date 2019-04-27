package com.myself.everything.core.index;

import com.myself.everything.core.dao.DataSourceFactory;
import com.myself.everything.core.dao.impl.FileIndexDaoImpl;
import com.myself.everything.core.index.impl.FileScanImpl;
import com.myself.everything.core.interceptor.FileInterceptor;
import com.myself.everything.core.interceptor.impl.FileIndexInterceptor;
import com.myself.everything.core.interceptor.impl.FilePrinterInterceptor;
import com.myself.everything.core.model.Thing;

public interface FileScan {
    void index(String path);//建立索引，遍历path

    void interceptor(FileInterceptor interceptor);//遍历的拦截器


    public static void main(String[] args) {
        DataSourceFactory.initDatabase();
        FileScanImpl scan = new FileScanImpl();
        FileInterceptor printerInterceptor = new FilePrinterInterceptor();
        scan.interceptor(printerInterceptor);
        FileInterceptor fileIndexInterceptor = new FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.dataSource()));
        scan.interceptor(fileIndexInterceptor);
        scan.index("D:\\学年论文");
    }
}
