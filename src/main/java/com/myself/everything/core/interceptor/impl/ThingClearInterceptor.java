package com.myself.everything.core.interceptor.impl;

import com.myself.everything.core.dao.FileIndexDao;
import com.myself.everything.core.interceptor.ThingInterceptor;
import com.myself.everything.core.model.Thing;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThingClearInterceptor implements ThingInterceptor, Runnable {//为了让线程能够处理 清理工作

    //队列里边存放要删除的Thing
    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);

    private final FileIndexDao fileIndexDao;

    public ThingClearInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(Thing thing) {
        this.queue.add(thing); //将要删除的thing文件放到队列中
    }

    @Override
    public void run() {
        while (true) {
            Thing thing = this.queue.poll(); //从队列中取要删除的thing
            if (thing != null) { //由于队列可能为空，在队列不为空的情况下，删除thing
                fileIndexDao.delete(thing);
            }
            //1. 优化 批量删除 将delete改为批量删除 todo
            //List<Thing> thingList=new ArrayList<>();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
