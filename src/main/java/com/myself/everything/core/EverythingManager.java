package com.myself.everything.core;

import com.myself.everything.config.EverythingConfig;
import com.myself.everything.core.dao.DataSourceFactory;
import com.myself.everything.core.dao.FileIndexDao;
import com.myself.everything.core.dao.impl.FileIndexDaoImpl;
import com.myself.everything.core.index.FileScan;
import com.myself.everything.core.index.impl.FileScanImpl;
import com.myself.everything.core.interceptor.ThingInterceptor;
import com.myself.everything.core.interceptor.impl.FileIndexInterceptor;
import com.myself.everything.core.interceptor.impl.ThingClearInterceptor;
import com.myself.everything.core.model.Condition;
import com.myself.everything.core.model.Thing;
import com.myself.everything.core.search.FileSearch;
import com.myself.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EverythingManager {
    private static volatile EverythingManager manager;
    private FileSearch fileSearch;
    private FileScan fileScan;
    private ExecutorService executorService;

    //清理删除的文件
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread;
    private AtomicBoolean backgroundClearThreadState = new AtomicBoolean(false);


    private EverythingManager() {
        this.initComponent();
    }

    //单例
    public static EverythingManager getInstance() {
        if (manager == null) {
            synchronized (EverythingManager.class) {
                if (manager == null) {
                    manager = new EverythingManager();
                }
            }
        }
        return manager;
    }

    private void initComponent() {

        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();

        //检查当前文件中是否包含数据库文件，如果没有就初始化数据库（everything.mv.db）
        checkDatabase();

        //业务层的对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);

        this.fileScan = new FileScanImpl();
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));

        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setDaemon(true);//将清理线程(用户线程)变成守护线程
        this.backgroundClearThread.setName("Thread-Thing-Clear");

    }

    private void checkDatabase() {
        String fileName = EverythingConfig.getInstance().getH2IndexPath() + ".mv.db";
        File dbFile = new File(fileName);
        if (dbFile.isFile() && !dbFile.exists()) {
            DataSourceFactory.initDatabase();
        }
    }


    //检索
    public List<Thing> search(Condition condition) {
        //TODO 扩展
        //Stream 流式处理 JDK8
        return this.fileSearch.search(condition).stream().filter(thing -> {
            String path = thing.getPath();
            File f = new File(path);
            boolean flag = f.exists();
            if (!flag) {
                //做删除
                thingClearInterceptor.apply(thing);
            }
            return flag;
        }).collect(Collectors.toList());
    }

    //索引
    public void buildIndex() {
//        initOrResetDatabase();
        Set<String> directories = EverythingConfig.getInstance().getIncludePath();//C: D: E:
        //多线程处理 -> 线程池
        //用目录的数量作为线程池的线程数量
        //最好给线程起个名字
        if (this.executorService == null) {
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                private final AtomicInteger threadId = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Scan-" + threadId.getAndIncrement());//用0开始，用完后自加
                    return thread;
                }
            });
        }

        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());//防止被修改，加上final

        System.out.println("Build index start ...");
        for (String path : directories) {
            this.executorService.submit(() -> {
                EverythingManager.this.fileScan.index(path);
                //当前任务完成，值-1（此磁盘索引已建立完成）
                countDownLatch.countDown();
            });
        }
        //阻塞，直到任务完成，值变成0
        try {
            countDownLatch.await();//阻塞，直到线程都处理完了，结束阻塞
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Build index complete ...");
    }

    //启动清理线程
    public void startBackgroundClearThread() {
        if (this.backgroundClearThreadState.compareAndSet(false, true)) {//防止多线程下的资源竞争
            this.backgroundClearThread.start();
        } else {
            System.out.println("can not start backgroundClearThead repeatly");
        }
    }

}
