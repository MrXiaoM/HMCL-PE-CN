package com.tungsten.hmclpe.sweetrice.task;

import com.tungsten.hmclpe.sweetrice.event.Event;

/**
 *
 * @author huang
 */
public class TaskEvent extends Event {

    private final Task<?> task;
    private final boolean failed;

    public TaskEvent(Object source, Task<?> task, boolean failed) {
        super(source);
        this.task = task;
        this.failed = failed;
    }

    public Task<?> getTask() {
        return task;
    }

    public boolean isFailed() {
        return failed;
    }

}