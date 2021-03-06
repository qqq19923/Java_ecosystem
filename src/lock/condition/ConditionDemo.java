package lock.condition;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jiangyunxiong on 2018/6/20.
 * <p>
 * 条件变量Condition锁
 */
public class ConditionDemo {

    private ReentrantLock lock = new ReentrantLock();
    private Condition take = lock.newCondition();
    private Condition put = lock.newCondition();

    private BlockingQueue queue = new ArrayBlockingQueue<>(10000);

    private int capacity = 5;
    private int i = 1;

    /**
     * 往队列里面插入数据
     */
    public void put() {
        lock.lock();
        System.out.println(String.format("【put】i=%d 获取锁!!!", i));
        try {
            while (queue.size() == capacity) {
                System.out.println(String.format("【put】插入 %d 时队列满，执行put.await！队列值：【%s】", i,
                        queue.toArray()));
                put.await();
            }
            System.out.println(String.format("【put】插入 %d 到队列", i));
            i++;
            // 读操作唤醒
            take.signal();
        } catch (Exception e) {

        } finally {
            System.out.println(String.format("【put】i=%d 释放锁!!!", i - 1));
            lock.unlock();
        }
    }

    /**
     * 从队列读数据
     */
    public void take() {
        lock.lock();
        System.out.println("【take】获取锁！");

        int data = 0;
        try {
            while (queue.size() == 0) {
                System.out.println("【take】执行take.await！");
                take.await();
            }
            data = (int) queue.take();
            System.out.println(String.format("【take】从队列读取值 %d ,队列值：【%s】", data, queue.toArray()));

            // 写操作唤醒
            put.signal();
        } catch (Exception e) {
        } finally {
            System.out.println(String.format("【take】i=%d 释放锁!!!", data));
            lock.unlock();
        }
    }
}
