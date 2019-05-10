package com.myself.everything.core.interceptor.impl;

import com.myself.everything.core.dao.FileIndexDao;
import com.myself.everything.core.interceptor.ThingInterceptor;
import com.myself.everything.core.model.Thing;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThingClearInterceptor implements ThingInterceptor, Runnable {

    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);

    private final FileIndexDao fileIndexDao;

    public ThingClearInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(Thing thing) {
        this.queue.add(thing);
    }

    @Override
    public void run() {
        while (true) {
            Thing thing = this.queue.poll();
            if (thing != null) {
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
