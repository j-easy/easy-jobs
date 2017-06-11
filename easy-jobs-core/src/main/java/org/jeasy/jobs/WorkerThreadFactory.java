package org.jeasy.jobs;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

class WorkerThreadFactory implements ThreadFactory {

    private final AtomicLong count = new AtomicLong(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("worker-thread-" + count.incrementAndGet());
        return thread;
    }
}
