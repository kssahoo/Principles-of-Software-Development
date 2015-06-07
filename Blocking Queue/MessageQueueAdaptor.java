package cs601.blkqueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class MessageQueueAdaptor<T> implements MessageQueue<T>
{
    private volatile int size;
    private volatile ArrayBlockingQueue<T> buffer;
	MessageQueueAdaptor(int size)
    {
        this.size = size;
        buffer = new ArrayBlockingQueue<T>(size);
    }

	@Override
	public void put(T o) throws InterruptedException
    {
        buffer.put(o);
	}


	@Override
	public T take() throws InterruptedException
    {

		return buffer.take();

	}
}
