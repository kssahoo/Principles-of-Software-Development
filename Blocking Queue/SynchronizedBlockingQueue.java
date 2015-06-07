package cs601.blkqueue;

import java.util.*;

public class SynchronizedBlockingQueue<T> implements MessageQueue<T>
{

    private volatile int size;
    private volatile List<T> buffer;
	public SynchronizedBlockingQueue(int size)
    {
        this.size = size;
        buffer = Collections.synchronizedList(new ArrayList<T>());
	}

	@Override
	public synchronized void put(T o) throws InterruptedException
    {
        while(buffer.size() == size)
        {
            wait();

        }

        System.out.println("adding "+o);
        buffer.add(o);
        notifyAll();
    }

	@Override
	public synchronized T take() throws InterruptedException
    {
        Thread.currentThread().getStackTrace();
        while(buffer.isEmpty())
        {
            wait();
        }


        T value = buffer.get(0);
        buffer.remove(0);
        notifyAll();
        System.out.println("removing "+value);
        return value;

	}
}
