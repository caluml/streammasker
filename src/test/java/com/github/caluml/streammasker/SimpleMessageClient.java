package com.github.caluml.streammasker;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * Simple message client. Reads from stdin, and writes to the MaskingOutputStream.
 */
public class SimpleMessageClient {

	public static void main(String[] args) throws Exception {

		final Socket socket = new Socket("127.0.0.1", 7733);
		System.out.println("Client: Opened socket to " + socket.getRemoteSocketAddress());
		final OutputStream outputStream = socket.getOutputStream();

		final MaskingOutputStream maskingOutputStream = new MaskingOutputStream(outputStream, 8, new SecureRandom(), 1000, TimeUnit.MILLISECONDS);
		new Thread(maskingOutputStream).start();

		final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(maskingOutputStream));

		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Client: Waiting for input");
		String line;
		do {
			line = bufferedReader.readLine();
			bufferedWriter.write(line + "\n");
			bufferedWriter.flush();
			System.out.println("Server: Wrote " + line.length() + " bytes: " + line);
		} while (line != null);
	}

}
