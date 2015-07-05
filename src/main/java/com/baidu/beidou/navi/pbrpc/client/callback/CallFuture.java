package com.baidu.beidou.navi.pbrpc.client.callback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.baidu.beidou.navi.pbrpc.exception.TimeoutException;
import com.baidu.beidou.navi.pbrpc.exception.client.PbrpcException;

/**
 * ClassName: CallFuture <br/>
 * Function: 回调的future实现
 * 
 * @author Zhang Xu
 */
public class CallFuture<T> implements Future<T>, Callback<T> {

    /**
     * 内部回调用的栅栏
     */
    private final CountDownLatch latch = new CountDownLatch(1);

    /**
     * 调用返回结果
     */
    private T result = null;

    /**
     * 调用错误信息
     */
    private Throwable error = null;

    /**
     * Creates a new instance of CallFuture.
     */
    private CallFuture() {

    }

    /**
     * 静态创建方法
     * 
     * @return
     */
    public static <E> CallFuture<E> newInstance() {
        return new CallFuture<E>();
    }

    /**
     * Sets the RPC response, and unblocks all threads waiting on {@link #get()} or {@link #get(long, TimeUnit)}.
     * 
     * @param result
     *            the RPC result to set.
     */
    public void handleResult(T result) {
        this.result = result;
        latch.countDown();
    }

    /**
     * Sets an error thrown during RPC execution, and unblocks all threads waiting on {@link #get()} or
     * {@link #get(long, TimeUnit)}.
     * 
     * @param error
     *            the RPC error to set.
     */
    public void handleError(Throwable error) {
        this.error = error;
        latch.countDown();
    }

    /**
     * Gets the value of the RPC result without blocking. Using {@link #get()} or {@link #get(long, TimeUnit)} is
     * usually preferred because these methods block until the result is available or an error occurs.
     * 
     * @return the value of the response, or null if no result was returned or the RPC has not yet completed.
     */
    public T getResult() {
        return result;
    }

    /**
     * Gets the error that was thrown during RPC execution. Does not block. Either {@link #get()} or
     * {@link #get(long, TimeUnit)} should be called first because these methods block until the RPC has completed.
     * 
     * @return the RPC error that was thrown, or null if no error has occurred or if the RPC has not yet completed.
     */
    public Throwable getError() {
        return error;
    }

    /**
     * @see java.util.concurrent.Future#get()
     */
    public T get() throws InterruptedException {
        latch.await();
        if (error != null) {
            throw new PbrpcException("Error occurrs due to " + error.getMessage(), error);
        }
        return result;
    }

    /**
     * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
     */
    public T get(long timeout, TimeUnit unit) {
        try {
            if (latch.await(timeout, unit)) {
                if (error != null) {
                    throw new PbrpcException("Error occurrs due to " + error.getMessage(), error);
                }
                return result;
            } else {
                throw new TimeoutException("CallFuture async get time out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("CallFuture is interuptted", e);
        }
    }

    /**
     * Waits for the CallFuture to complete without returning the result.
     * 
     * @throws InterruptedException
     *             if interrupted.
     */
    public void await() throws InterruptedException {
        latch.await();
    }

    /**
     * Waits for the CallFuture to complete without returning the result.
     * 
     * @param timeout
     *            the maximum time to wait.
     * @param unit
     *            the time unit of the timeout argument.
     * @throws InterruptedException
     *             if interrupted.
     * @throws TimeoutException
     *             if the wait timed out.
     */
    public void await(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (!latch.await(timeout, unit)) {
            throw new TimeoutException();
        }
    }

    /**
     * @see java.util.concurrent.Future#cancel(boolean)
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    /**
     * @see java.util.concurrent.Future#isCancelled()
     */
    public boolean isCancelled() {
        return false;
    }

    /**
     * @see java.util.concurrent.Future#isDone()
     */
    public boolean isDone() {
        return latch.getCount() <= 0;
    }

}