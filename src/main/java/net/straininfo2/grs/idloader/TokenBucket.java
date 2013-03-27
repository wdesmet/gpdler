package net.straininfo2.grs.idloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Token Bucket implementation, using a BlockingDeque as the backing store. It
 * uses a polling thread in the background to fill the bucket. This thread
 * should be stopped using the stop() method.
 * 
 * It's a pretty coarse method, since the tokens are added approximately every
 * second. This means that, if the task you are rate throttling with this
 * method is very quick (say, outputting to console), you will see very bursty
 * behaviour (output every second or so, of 3 executions in quick succession).
 * This is okay for the task this class is designed for (web requests, with
 * comparatively low rates of requests), but you might want the filler thread
 * to have smaller granularity for some tasks.
 *
 * A {@link java.util.concurrent.DelayQueue} might fit better, but this
 * implementation does work.
 */
public class TokenBucket {

    private final static Logger logger = LoggerFactory.getLogger(TokenBucket.class);

    private final BlockingDeque<Token> store;

    private final Thread fillThread;

    private volatile boolean run;

    private final static Token THE_TOKEN = new Token();

    private class Filler implements Runnable {

        private final long start;
        
        private long lastIndex;

        private final int rate;

        public Filler(long start, int rate) {
            this.start = start;
            this.rate = rate;
            this.lastIndex = 0;
        }

        @Override
        public void run() {
            while (run) {
                try {
                    long index = (System.currentTimeMillis() - start) / 1000;
                    if (index > lastIndex) {
                        while (lastIndex < index) {
                            for (int i = 0; i < rate; i++) {
                                // this will block if the store is full
                                store.put(THE_TOKEN);
                            }
                            lastIndex++;
                        }
                    }
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    // not a problem, just a good way to end the thread
                    // after an interrupt, the counts of how many tokens to
                    // add will probably no longer be accurate, but small
                    // variations are okay and interrupts are only supposed to
                    // happen at stop() time anyway.
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private final static class Token {
        // just a fancily named Object
    }

    /**
     * Construct a new token bucket. It will be given a maximum of
     * {@code 10 * rate} tokens.
     *
     * rate - number of new tokens to add to the bucket per second
     */
    @SuppressWarnings("SameParameterValue")
    public TokenBucket(int rate) {
        this.store = new LinkedBlockingDeque<>(10 * rate);
        this.run = true;
        this.fillThread = new Thread(new Filler(System.currentTimeMillis(),
                    rate));
        this.fillThread.start();
    }

    public void take() throws InterruptedException {
        store.take(); // should block till a token is available
    }

    public void stop() {
        this.run = false;
        while (fillThread.isAlive()) {
            this.fillThread.interrupt();
        }
    }

    @Override
    protected void finalize() throws Throwable { 
        // user should call stop(), but might not
        try {
            this.stop();
        } finally {
            super.finalize();
        }
    }
}