package ys.app.feed.utils;

/**
 * ResultCallback可定义为泛型类
 */
public abstract class ResultCallback<T>{
    public abstract void onResponse(T response);
    public abstract void onFailure(Exception e);
}
