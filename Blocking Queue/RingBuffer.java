package cs601.blkqueue;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

public class RingBuffer<T> implements MessageQueue<T>
{
	private final AtomicLong w = new AtomicLong(-1);	// just wrote location
	private final AtomicLong r = new AtomicLong(0);		// about to read location
    private T[] ringBuffer;
    private volatile int n;

	public RingBuffer(int n)
    {
        this.n = n;
        if(isPowerOfTwo(n) == false)
        {
            throw new IllegalArgumentException();
        }
        ringBuffer = (T[]) new Object[n];
	}

	// http://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
	static boolean isPowerOfTwo(int v)
    {
		if (v<0) return false;
		v = v - ((v >> 1) & 0x55555555);                    // reuse input as temporary
		v = (v & 0x33333333) + ((v >> 2) & 0x33333333);     // temp
		int onbits = ((v + (v >> 4) & 0xF0F0F0F) * 0x1010101) >> 24; // count
		// if number of on bits is 1, it's power of two, except for sign bit
		return onbits==1;
	}

	@Override
	public void put(T v) throws InterruptedException
    {
        while((w.get()+1 - r.get()) == n)
        {
            LockSupport.parkNanos(1);
        }
        ringBuffer[(int)((w.get()+1) & (n-1))] = v;
        w.getAndIncrement();
	}

	@Override
	public T take() throws InterruptedException
    {
        while(!(w.get() >= r.get()))
        {
            LockSupport.parkNanos(1);
        }
        T value = ringBuffer[(int)(r.get() & (n-1)) ];
        r.getAndIncrement();
        return value;
	}

}
