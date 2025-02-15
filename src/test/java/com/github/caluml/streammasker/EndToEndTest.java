package com.github.caluml.streammasker;

import org.apache.commons.io.IOUtils;
import org.awaitility.Awaitility;
import org.junit.Test;

import java.util.Random;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class EndToEndTest {

	private final Random random = new Random();

	@Test
	public void End_to_end_test() throws Exception {
		// Given
		int pulseSize = 3;
		byte[] payload = random(7);
		InputOutputStreamConnector connector = new InputOutputStreamConnector();

		MaskingOutputStream output = new MaskingOutputStream(connector.getOutputStream(), pulseSize, random, 10, MILLISECONDS);
		Thread outputThread = new Thread(output);
		outputThread.start();

		MaskingInputStream input = new MaskingInputStream(connector.getInputStream(), pulseSize);
		Thread inputThread = new Thread(input);
		inputThread.start();

		Awaitility.await().until(() -> inputThread.isAlive() && outputThread.isAlive());

		// When
		for (byte b : payload) {
			output.write(b);
		}

		// Then
		byte[] readBytes = IOUtils.readFully(input, payload.length);
		assertThat(readBytes).containsExactly(payload);
	}

	private byte[] random(int size) {
		byte[] bytes = new byte[size];
		random.nextBytes(bytes);
		return bytes;
	}

}
