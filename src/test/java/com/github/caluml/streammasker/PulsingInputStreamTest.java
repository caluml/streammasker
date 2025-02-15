package com.github.caluml.streammasker;

import org.junit.Test;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class PulsingInputStreamTest {

	@Test(expected = IllegalStateException.class)
	public void PulsingInputStream_throws_IllegalStateException_if_not_running_in_a_thread() {
		new PulsingInputStream(null, 1).read(new byte[1], 0, 1);
	}
}
