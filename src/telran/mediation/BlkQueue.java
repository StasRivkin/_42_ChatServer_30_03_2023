package telran.mediation;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlkQueue<T> implements IBlkQueue<T> {
    Lock locker = new ReentrantLock();
    Condition waitingProd = locker.newCondition();
    Condition waitingCons = locker.newCondition();
    private final int maxSize;
    private final LinkedList<T> queueMsg;

    public BlkQueue(int maxSize) {
        this.maxSize = maxSize;
        queueMsg = new LinkedList<>();
    }

    @Override
    public void push(T message) {
        locker.lock();
        try {
            while (queueMsg.size() > maxSize) {
                try {
                    waitingProd.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
            queueMsg.add(message);
            waitingCons.signal();
        } finally {
            locker.unlock();
        }
    }

    @Override
    public T pop() {
        locker.lock();
        try {
            while (queueMsg.isEmpty()) {
                try {
                    waitingCons.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T message = queueMsg.removeLast();
            waitingProd.signal();
            return message;
        } finally {
            locker.unlock();
        }
    }
}