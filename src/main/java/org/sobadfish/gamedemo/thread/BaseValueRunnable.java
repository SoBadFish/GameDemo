package org.sobadfish.gamedemo.thread;

/**
 * @author Sobadfish
 * @date 2023/1/13
 */
public abstract class BaseValueRunnable implements Runnable {

    public Object[] value;

    public BaseValueRunnable(Object... value) {
        this.value = value;
    }
}
