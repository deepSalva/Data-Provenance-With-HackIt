package org.qcri.hackit.spark.listener;


import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.scheduler.SparkListenerJobEnd;
import org.apache.spark.scheduler.SparkListenerJobStart;
import org.apache.spark.scheduler.SparkListenerTaskEnd;
import org.apache.spark.scheduler.TaskInfo;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class HackItListener extends SparkListener {
    transient public long start, end;
    transient public String name;
    transient public Map<String, Long> tasks;

    @Override
    public void onJobStart(SparkListenerJobStart jobStart) {
        start = jobStart.time();
        this.tasks = new ConcurrentHashMap<>();
    }

    @Override
    public void onJobEnd(SparkListenerJobEnd jobEnd) {
        end = jobEnd.time();
    }

    @Override
    public void onTaskEnd(SparkListenerTaskEnd taskEnd) {
        TaskInfo tmp = taskEnd.taskInfo();
        this.tasks.put(tmp.id(), tmp.duration());
    }

}
