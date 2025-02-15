package com.github.caluml.streammasker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class PulsingOutputStream extends OutputStream implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final OutputStream outputStream;
	private final int pulseSize;
	private final Random random;
	private final long interval;

	private final Queue<Byte> queue = new ConcurrentLinkedQueue<>();

	private long nextPulse;

	private volatile boolean running;

	/**
	 * OutputStream that sends real data, or junk data if there is no real data at random intervals.
	 * Is only useful if it is sent over an encrypted link, which is not handled by this class.
	 *
	 * @param outputStream the {@link OutputStream} that this class wraps.
	 * @param pulseSize    the number of bytes that are sent in each pulse.
	 * @param random       the {@link Random} to use
	 */
	public PulsingOutputStream(OutputStream outputStream,
														 int pulseSize,
														 Random random,
														 int units,
														 TimeUnit unit) {
		this.outputStream = outputStream;
		this.pulseSize = pulseSize;
		if (!(random instanceof SecureRandom)) {
			logger.warn("Not using SecureRandom");
		}
		this.random = random;
		interval = unit.toMillis(units);
	}

	/**
	 * Adds data to be sent. Doesn't guarantee when it will be sent.
	 */
	@Override
	public void write(int b) {
		if (!running) {
			throw new IllegalStateException(this.getClass().getSimpleName() + " is not running as a thread");
		}

		logger.debug("Added {} to queue", b);
		queue.add((byte) b);
	}

	@Override
	public void run() {
		running = true;
		logger.debug("Set running = true");
		nextPulse = System.currentTimeMillis();

		while (running) {
			byte[] pulse = getPulse();
			logger.debug("Got pulse {}", Arrays.toString(pulse));

			waitForNextTimeslot();

			writePulse(pulse);
		}

		logger.warn("{}: Exiting run()", this);
	}

	private byte[] getPulse() {
		byte[] pulse = new byte[pulseSize + 2]; // size + short
		random.nextBytes(pulse);
		setPulseLength(pulse, 0); // Length of 0 signifies junk data

		logger.debug("Queue size is {}", queue.size());
		if (queue.isEmpty()) {
			logger.debug("Queue is empty - returning random data");
			return pulse;
		}

		int i;
		for (i = 0; i < pulseSize; i++) {
			Byte b = queue.poll();
			if (b == null) {
				break;
			}
			pulse[i + 2] = b;
			logger.debug("Set pulse[{}] to {}", i + 2, b);
		}
		setPulseLength(pulse, i);

		return pulse;
	}

	private void setPulseLength(final byte[] pulse,
															final int length) {
		pulse[0] = (byte) (length / 256);
		pulse[1] = (byte) length;
		logger.debug("Set message length to {}", length);
	}

	/**
	 * Blocks until the next timeslot has arrived
	 */
	private void waitForNextTimeslot() {
		logger.debug("Waiting for next pulse");
		long millisToNextPulse = nextPulse - System.currentTimeMillis();
		logger.debug("millisToNextPulse {}", millisToNextPulse);
		nextPulse = nextPulse + interval;
		logger.debug("nextPulse {}", nextPulse);
		if (millisToNextPulse < 0) return;

		try {
			Thread.sleep(millisToNextPulse);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		logger.debug("Reached next pulse");
	}

	private void writePulse(byte[] pulse) {
		try {
			outputStream.write(pulse);
			logger.debug("Wrote pulse {}", Arrays.toString(pulse));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		super.close();
		running = false;
		Thread.currentThread().interrupt();
	}
}
