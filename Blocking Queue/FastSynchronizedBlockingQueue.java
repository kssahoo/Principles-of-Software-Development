package cs601.blkqueue;
import java.util.*;
public class FastSynchronizedBlockingQueue<T> implements MessageQueue<T>
{
    private volatile int size;
    private volatile List<T> buffer;
	public FastSynchronizedBlockingQueue(int size)
    {
        this.size = size;
        buffer = new ArrayList<T>();
	}

	@Override
	public synchronized void put(T o) throws InterruptedException
    {
        while(buffer.size() == size)
        {
            wait();
        }
        int prevBufferSize = buffer.size();
        buffer.add(o);
        int currentSize = buffer.size();
        if(prevBufferSize == 0)
        {
            notifyAll();
        }
	}

	@Override
	public synchronized T take() throws InterruptedException
    {
        while(buffer.isEmpty())
        {
            wait();
        }
        int prevBufferSize = buffer.size();
        T value = buffer.get(0);
        buffer.remove(0);
        int currentSize = buffer.size();
        if(prevBufferSize == size)
        {
            notifyAll();
        }

        return value;
	}
}
