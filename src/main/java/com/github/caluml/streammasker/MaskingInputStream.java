package com.github.caluml.streammasker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MaskingInputStream extends InputStream implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final InputStream inputStream;
	private final int pulseSize;

	private final Queue<Byte> queue = new ConcurrentLinkedQueue<>();

	private volatile boolean running;

	public MaskingInputStream(final InputStream inputStream,
														final int pulseSize) {
		this.inputStream = inputStream;
		this.pulseSize = pulseSize;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			byte[] pulse = new byte[pulseSize + 2];
			final int bytesRead = readPulse(pulse);
			if (bytesRead == -1) {
				break;
			}

			if (bytesRead != pulseSize + 2) {
				logger.warn("bytesRead = {}!", bytesRead);
			}

			final short pulseLength = getPulseLength(pulse);
			if (pulseLength > 0) {
				addMessageToQueue(pulse, pulseLength);
			}
		}
		logger.warn("{}: Exiting run()", this);
	}

	@Override
	public int read(final byte[] buffer,
									final int offset,
									final int length) {
		if (!running) {
			throw new IllegalStateException(this.getClass().getSimpleName() + " is not running as a thread");
		}

		logger.debug("Waiting for data");
		while (queue.isEmpty()) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		logger.debug("Data in queue");

		int numBytesRead = 0;
		for (int i = 0; i < buffer.length; i++) {
			final Byte polled = queue.poll();
			if (polled == null) {
				break;
			}
			logger.debug("Adding {} to buffer[{}]", polled, i);
			buffer[i] = polled;
			numBytesRead++;
		}

		return numBytesRead;
	}

	private void addMessageToQueue(final byte[] message,
																 final short length) {
		for (int i = 2; i < length + 2; i++) {
			queue.add(message[i]);
		}
	}

	private short getPulseLength(final byte[] pulse) {
		return (short) ((pulse[0] * 256) + pulse[1]);
	}

	private int readPulse(final byte[] pulse) {
		try {
			return inputStream.read(pulse);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int available() {
		return queue.size();
	}

	@Override
	public void close() throws IOException {
		super.close();
		running = false;
		Thread.currentThread().interrupt();
	}

	@Override
	public synchronized void mark(final int readLimit) {
		super.mark(readLimit);
	}


	@Override
	public int read() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public int read(final byte[] b) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public long skip(final long n) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public synchronized void reset() {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public boolean markSupported() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
