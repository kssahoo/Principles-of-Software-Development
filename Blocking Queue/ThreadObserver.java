package cs601.blkqueue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

/** A runnable class that attaches to another thread and wakes up
 *  at regular intervals to determine that thread's state. The goal
 *  is to figure out how much time that thread is blocked, waiting,
 *  or sleeping.
 */
class ThreadObserver implements Runnable {
    protected final Map<String, Long> histogram = new HashMap<String, Long>();
    protected int numEvents = 0;
    protected int blocked = 0;
    protected int waiting = 0;
    protected int sleeping = 0;
    Thread thread;
    long period;
    boolean isDone = false;
    public ThreadObserver(Thread threadToMonitor, long periodInNanoSeconds) {
        this.thread = threadToMonitor;
        this.period = periodInNanoSeconds;
    }

    @Override
    public void run() {
        while (!isDone){
            StackTraceElement[] stack = thread.getStackTrace();
            if(stack.length > 0) {
                String method = stack[0].getMethodName();
                if (histogram.containsKey(method)) {
                    histogram.put(method, histogram.get(method) + 1);
                } else {
                    histogram.put(method, 1L);
                }
                numEvents++;
                switch (thread.getState()) {
                    case BLOCKED:
                        blocked++;
                        break;
                    case WAITING:
                        waiting++;
                        break;
                    case TIMED_WAITING:
                        sleeping++;
                        break;
                }
                LockSupport.parkNanos(1000L);
            }
        }

    }

    public Map<String, Long> getMethodSamples() { return histogram; }

    public void terminate() { isDone =true; }

    public String toString() {
        return String.format("(%d blocked + %d waiting + %d sleeping) / %d samples = %1.2f%% wasted",
                blocked,
                waiting,
                sleeping,
                numEvents,
                100.0*(blocked + waiting + sleeping)/numEvents);
    }
}
