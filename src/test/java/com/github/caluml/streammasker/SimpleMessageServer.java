package com.github.caluml.streammasker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple message server. Wraps a standard network socket in the {@link PulsingInputStream}, and reads lines from that.
 */
public class SimpleMessageServer {

	public static void main(String[] args) throws Exception {

		final ServerSocket serverSocket = new ServerSocket(7733);
		System.out.println("Server: Listening on 7733");
		final Socket socket = serverSocket.accept();
		System.out.println("Server: Accepted connection from " + socket.getRemoteSocketAddress());
		final InputStream inputStream = socket.getInputStream();

		final PulsingInputStream pulsingInputStream = new PulsingInputStream(inputStream, 8);
		new Thread(pulsingInputStream).start();
		Thread.sleep(100); // allow running to be set to true

		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pulsingInputStream));
		String line;
		do {
			line = bufferedReader.readLine();
			System.out.println("Server: Received " + line.length() + " bytes: " + line);
		} while (line != null);

		System.out.println("Server: Exiting");
	}

}
