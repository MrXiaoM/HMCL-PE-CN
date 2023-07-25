package com.tungsten.hmclpe.sweetrice.utils.function;

public interface ExceptionalPredicate<T, E extends Exception> {
    boolean test(T t) throws E;
}
