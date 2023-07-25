package com.tungsten.hmclpe.sweetrice.utils.gson.tools;

public @interface JsonSubtype {
    Class<?> clazz();

    String name();
}
