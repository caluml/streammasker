package com.github.caluml.streammasker;

import org.junit.Test;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

public class PulsingOutputStreamTest {

	@Test(expected = IllegalStateException.class)
	public void PulsingOutputStream_throws_IllegalStateException_if_not_running_in_a_thread() throws IOException {
		new PulsingOutputStream(null, 1, null, 1, SECONDS).write(new byte[1]);
	}
}
