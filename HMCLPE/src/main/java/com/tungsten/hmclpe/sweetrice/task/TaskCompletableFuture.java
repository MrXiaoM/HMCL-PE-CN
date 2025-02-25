package com.tungsten.hmclpe.sweetrice.task;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface TaskCompletableFuture {

    <T> CompletableFuture<T> one(Task<T> task);

    CompletableFuture<?> all(Collection<Task<?>> tasks);
}