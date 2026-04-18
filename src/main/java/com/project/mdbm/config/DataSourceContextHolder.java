package com.project.mdbm.config;

public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setDataSourceKey(String key) {
        CONTEXT.set(key);
    }

    public static String getDataSourceKey() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}