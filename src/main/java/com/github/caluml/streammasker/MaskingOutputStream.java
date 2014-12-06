package com.github.caluml.streammasker;

import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class MaskingOutputStream extends OutputStream implements Runnable {

    private final OutputStream outputStream;
    private final int size;
    private final SecureRandom random;
    private final Queue<Byte> queue = new ConcurrentLinkedQueue<Byte>();
    private final long interval;
    private long lastPulse;
    private volatile boolean running;
    private volatile boolean debug;


    /**
     * OutputStream that sends real data, or junk data if there is no real data at random intervals.
     * Is only useful if it is sent over an encrypted link, which is not handled by this class.
     *
     * @param outputStream the output stream that this class wraps.
     * @param size         the number of bytes that are sent in each pulse.
     * @param random       the SecureRandom to use
     */
    public MaskingOutputStream(OutputStream outputStream, int size, SecureRandom random, int units, TimeUnit unit) {
        this.outputStream = outputStream;
        this.size = size;
        this.random = random;
        interval = unit.toMillis(units);
    }

    /**
     * Adds data to be sent. Doesn't guarantee when it will be sent.
     *
     * @param b
     * @throws java.io.IOException
     * @throws IllegalArgumentException if this class isn't running as a thread
     */
    @Override
    public void write(int b) throws IOException {
        if (!running) {
            throw new IllegalArgumentException("MaskingOutputStream is not running as a thread");
        }
        if (debug) System.out.println("Added " + b + " to queue");
        queue.add((byte) b);
    }

    @Override
    public void run() {
        running = true;
        if (debug) System.out.println("Set running = true");
        lastPulse = System.currentTimeMillis();
        while (running) {
            byte[] message = getMessage();
            if (debug) System.out.println("Got message " + Arrays.toString(message));

            waitForNextPulse();

            writeMessage(message);
        }
        if (debug) System.err.println(this + ": Exiting run()");
    }

    private byte[] getMessage() {
        byte[] message = new byte[size + 2]; // size + short
        random.nextBytes(message);
        setMessageLength(message, 0);

        synchronized (queue) {
            if (debug) System.out.println("queue size is " + queue.size());
            if (queue.isEmpty()) {
                return message;
            }

            int i;
            for (i = 2; i < size + 2; i++) {
                Byte b = queue.poll();
                if (b != null) {
                    message[i] = b;
                    if (debug) System.out.println("Set pos " + i + " to " + b);
                } else {
                    break;
                }
            }
            setMessageLength(message, i - 2);
        }
        return message;
    }

    private void setMessageLength(final byte[] message, final int length) {
        // TODO: Make this work when length > 256
        message[0] = 0;
        message[1] = (byte) length;
        if (debug) System.out.println("Set message length to " + length);
    }

    /**
     * Blocks until the next timeslot has arrived
     */
    private void waitForNextPulse() {
        if (debug) System.out.println(new Date() + ": Waiting for next pulse");
        while (lastPulse > System.currentTimeMillis() - interval) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (debug) System.out.println(new Date() + ": Reached next pulse");
        lastPulse = lastPulse + interval;
    }

    private void writeMessage(byte[] message) {
        try {
            outputStream.write(message);
            if (debug) System.err.println("Wrote message " + Arrays.toString(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        running = false;
    }

    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
}
