package com.github.caluml.streammasker;

import org.junit.Test;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MaskingInputStreamTest {

	@Test(expected = IllegalStateException.class)
	public void MaskingInputStream_throws_IllegalStateException_if_not_running_in_a_thread() {
		new MaskingInputStream(null, 1).read(new byte[1], 0, 1);
	}
}
