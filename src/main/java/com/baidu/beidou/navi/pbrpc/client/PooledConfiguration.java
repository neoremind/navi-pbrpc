package com.baidu.beidou.navi.pbrpc.client;

import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * ClassName: PooledConfiguration <br/>
 * Function: socket连接池配置参数
 * 
 * @author Zhang Xu
 */
public class PooledConfiguration {

    /**
     * 控制池中空闲的对象的最大数量。 默认值是8，如果是负值表示没限制。
     */
    private int maxIdle = GenericObjectPool.DEFAULT_MAX_IDLE;

    /**
     * whenExhaustedAction如果是WHEN_EXHAUSTED_BLOCK，指定等待的毫秒数。<br/>
     * 如果maxWait是正数，那么会等待maxWait的毫秒的时间，超时会抛出NoSuchElementException异常 ；<br/>
     * 如果maxWait为负值，会永久等待。 maxWait的默认值是-1。
     */
    private long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;

    /**
     * 如果testOnBorrow被设置，pool会在borrowObject返回对象之前使用PoolableObjectFactory的validateObject来验证这个对象是否有效，要是对象没通过验证，这个对象会被丢弃，
     * 然后重新选择一个新的对象。 testOnBorrow的默认值是false，可以使用GenericObjectPool.DEFAULT_TEST_ON_BORROW;。
     * <p/>
     * 注意，对于长期idle的连接，服务端会默认关闭channel此时客户端并不知晓，因此不能使用已经失效的channel，为保证客户端可用，这里暂时使用这个策略每次borrow的时候都test
     */
    private boolean testOnBorrow = true;

    /**
     * 控制池中空闲的对象的最小数量。 默认值是0。
     */
    private int minIdle = GenericObjectPool.DEFAULT_MIN_IDLE;

    /**
     * 控制池中对象的最大数量。 默认值是8，如果是负值表示没限制。
     */
    private int maxActive = GenericObjectPool.DEFAULT_MAX_ACTIVE;

    /**
     * 如果testOnReturn被设置，pool会在returnObject的时候通过PoolableObjectFactory的validateObject方法验证对象，如果对象没通过验证，对象会被丢弃，不会被放到池中。
     * testOnReturn的默认值是false。
     */
    private boolean testOnReturn = GenericObjectPool.DEFAULT_TEST_ON_RETURN;

    /**
     * 指定idle对象是否应该使用PoolableObjectFactory的validateObject校验，如果校验失败，这个对象会从对象池中被清除。
     * 这个设置仅在timeBetweenEvictionRunsMillis被设置成正值（>0）的时候才会生效。 testWhileIdle的默认值是false。
     */
    private boolean testWhileIdle = GenericObjectPool.DEFAULT_TEST_WHILE_IDLE;

    /**
     * 指定驱逐线程的休眠时间。如果这个值不是正数（>0），不会有驱逐线程运行。 timeBetweenEvictionRunsMillis的默认值是-1。
     */
    private long timeBetweenEvictionRunsMillis = GenericObjectPool.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    /**
     * 设置驱逐线程每次检测对象的数量。 这个设置仅在timeBetweenEvictionRunsMillis被设置成正值（>0）的时候才会生效。 numTestsPerEvictionRun的默认值是3。
     */
    private int numTestsPerEvictionRun = GenericObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

    /**
     * 指定最小的空闲驱逐的时间间隔（空闲超过指定的时间的对象，会被清除掉）。 这个设置仅在timeBetweenEvictionRunsMillis被设置成正值（>0）的时候才会生效。
     * minEvictableIdleTimeMillis默认值是30分钟。
     */
    private long minEvictableIdleTimeMillis = GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    /**
     * 与minEvictableIdleTimeMillis类似，也是指定最小的空闲驱逐的时间间隔（空闲超过指定的时间的对象，会被清除掉），不过会参考minIdle的值，只有idle对象的数量超过minIdle的值，对象才会被清除。
     * 这个设置仅在timeBetweenEvictionRunsMillis被设置成正值
     * （>0）的时候才会生效，并且这个配置能被minEvictableIdleTimeMillis配置取代（minEvictableIdleTimeMillis配置项的优先级更高）。
     * softMinEvictableIdleTimeMillis的默认值是-1。
     */
    private long softMinEvictableIdleTimeMillis = GenericObjectPool.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    /**
     * 设置后进先出的池策略。pool可以被配置成LIFO队列（last-in-first-out）或FIFO队列（first-in-first-out），来指定空闲对象被使用的次序。 lifo的默认值是true。
     */
    private boolean lifo = GenericObjectPool.DEFAULT_LIFO;

    /**
     * 指定池中对象被消耗完以后的行为，有下面这些选择： WHEN_EXHAUSTED_FAIL 0 WHEN_EXHAUSTED_GROW 2 WHEN_EXHAUSTED_BLOCK 1
     * 如果是WHEN_EXHAUSTED_FAIL，当池中对象达到上限以后，继续borrowObject会抛出NoSuchElementException异常。
     * 如果是WHEN_EXHAUSTED_GROW，当池中对象达到上限以后，会创建一个新对象，并返回它。
     * 如果是WHEN_EXHAUSTED_BLOCK，当池中对象达到上限以后，会一直等待，直到有一个对象可用。这个行为还与maxWait有关
     * ，如果maxWait是正数，那么会等待maxWait的毫秒的时间，超时会抛出NoSuchElementException异常；如果maxWait为负值，会永久等待。
     * whenExhaustedAction的默认值是WHEN_EXHAUSTED_BLOCK，maxWait的默认值是-1。
     */
    private byte whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;

    /**
     * 获取对象池配置
     * 
     * @return
     */
    public GenericObjectPool.Config getPoolConfig() {
        GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
        // maxIdle为负数时，不对pool size大小做限制，此处做限制，防止保持过多空闲redis连接
        if (this.maxIdle >= 0) {
            poolConfig.maxIdle = this.maxIdle;
        }
        poolConfig.maxWait = this.maxWait;
        if (this.whenExhaustedAction >= 0 && this.whenExhaustedAction < 3) {
            poolConfig.whenExhaustedAction = this.whenExhaustedAction;
        }
        poolConfig.testOnBorrow = this.testOnBorrow;
        poolConfig.minIdle = this.minIdle;
        poolConfig.maxActive = this.maxActive;
        poolConfig.testOnReturn = this.testOnReturn;
        poolConfig.testWhileIdle = this.testWhileIdle;
        poolConfig.timeBetweenEvictionRunsMillis = this.timeBetweenEvictionRunsMillis;
        poolConfig.numTestsPerEvictionRun = this.numTestsPerEvictionRun;
        poolConfig.minEvictableIdleTimeMillis = this.minEvictableIdleTimeMillis;
        poolConfig.softMinEvictableIdleTimeMillis = this.softMinEvictableIdleTimeMillis;
        poolConfig.lifo = this.lifo;
        return poolConfig;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public long getSoftMinEvictableIdleTimeMillis() {
        return softMinEvictableIdleTimeMillis;
    }

    public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public boolean isLifo() {
        return lifo;
    }

    public void setLifo(boolean lifo) {
        this.lifo = lifo;
    }

    public byte getWhenExhaustedAction() {
        return whenExhaustedAction;
    }

    public void setWhenExhaustedAction(byte whenExhaustedAction) {
        this.whenExhaustedAction = whenExhaustedAction;
    }

}
