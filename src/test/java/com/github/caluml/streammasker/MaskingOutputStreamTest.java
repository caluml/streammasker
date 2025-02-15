package com.github.caluml.streammasker;

import org.junit.Test;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MaskingOutputStreamTest {

	@Test(expected = IllegalStateException.class)
	public void MaskingOutputStream_throws_IllegalStateException_if_not_running_in_a_thread() throws IOException {
		new MaskingOutputStream(null, 1, null, 1, SECONDS).write(new byte[1]);
	}
}
