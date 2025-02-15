package com.github.caluml.streammasker;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class InputOutputStreamConnector {

	private final PipedInputStream inputStream;
	private final PipedOutputStream outputStream;

	public InputOutputStreamConnector() {
		try {
			outputStream = new PipedOutputStream();
			inputStream = new PipedInputStream();
			outputStream.connect(inputStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public PipedInputStream getInputStream() {
		return inputStream;
	}

	public PipedOutputStream getOutputStream() {
		return outputStream;
	}
}
