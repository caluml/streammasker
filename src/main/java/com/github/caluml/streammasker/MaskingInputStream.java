package com.github.caluml.streammasker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MaskingInputStream extends InputStream implements Runnable {

    private final Queue<Byte> queue = new ConcurrentLinkedQueue<Byte>();

    private final InputStream inputStream;
    private final int size;
    private volatile boolean running;

    public MaskingInputStream(final InputStream inputStream, final int size) {
        this.inputStream = inputStream;
        this.size = size;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            byte[] message = new byte[size + 2]; // size + short
            final int bytesRead = readMessage(message);
            if (bytesRead == -1) {
                break;
            }

            // TODO: Don't assume size + 2!
            if (bytesRead != size + 2) {
                System.err.println("bytesRead = " + bytesRead + "!");
            }

            final short length = getLength(message);
            if (length > 0) {
                addMessageToQueue(message, length);
            }
        }
        System.err.println(this + ": Exiting run()");
    }

    private void addMessageToQueue(final byte[] message, final short length) {
        synchronized (queue) {
            for (int i = 2; i < length + 2; i++) {
                queue.add(message[i]);
            }
        }
    }

    private short getLength(final byte[] message) {
        // TODO: Make this work when length > 256
        return (short) message[1];
    }

    private int readMessage(final byte[] message) {
        try {
            final int read = inputStream.read(message);
            return read;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (!running) {
            throw new IllegalArgumentException("MaskingInputStream is not running as a thread");
        }
        // block until we can read data.
        while (queue.isEmpty()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        int read = 0;
        synchronized (queue) {
            for (int i = 0; i < b.length; i++) {
                final Byte polled = queue.poll();
                if (polled == null) {
                    break;
                }
                b[i] = polled;
                read++;
            }
        }
        return read;
    }

    @Override
    public int available() throws IOException {
        return queue.size();
    }

    @Override
    public int read(final byte[] b) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public long skip(final long n) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public synchronized void mark(final int readlimit) {
        super.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean markSupported() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
