streammasker
============

https://en.wikipedia.org/wiki/Numbers_station are interesting.<br>
They are widely believed to be the way spies are sent their instructions.<br>
They broadcast numbers all the time whether there are instructions in it or not. This means it's impossible to know when real data is being sent, because data is being sent all the time.<br>
The messages sent are probably encrypted using a https://en.wikipedia.org/wiki/One-time_pad.<br>
https://github.com/caluml/libxor/ is a  Java library providing OTP functionality which wraps Input/Output streams.  

You can find out more about number stations at https://priyom.org/

### What is this?
This is my attempt at writing wrappers for Java's Input/OutputStreams that essentially prevent someone viewing network traffic from knowing whether any information is being sent or not.

### How to use
Both wrappers need to run in threads to be able to send fake data when there is no real data to be sent.<br>
To wrap an OutputStream (sending 10 bytes (8 + 2) every 250 milliseconds)
```java
MaskingOutputStream maskingOutputStream = new MaskingOutputStream(outputStream, 8, new SecureRandom(), 250, TimeUnit.MILLISECONDS);
new Thread(maskingOutputStream).start();
```

To wrap an InputStream
```java
MaskingInputStream maskingInputStream = new MaskingInputStream(inputStream, 8);
new Thread(maskingInputStream).start();
```

### Notes
* There are a SimpleMessageServer and SimpleMessageClient to get an idea of how to use this. `tcpdump -X -npi any tcp dst port 7733` will show you the traffic. There are a couple of scripts to run the example server and client.
* Not all methods in MaskingInputStream have been overridden yet.
* Without encryption, this is pretty pointless, as it's trivial to see the data being sent/not being sent. Encryption isn't part of this library though.
* streammasker effectively limits and uses/wastes a fixed amount of bandwidth.
* It sends random data to mitigate against https://en.wikipedia.org/wiki/Known-plaintext_attack. (I don't know if it's actually necessary these days with modern ciphers like AES)

#### Isn't this a very inefficient way of transmitting data?
Yes. It wastes bandwidth, and is slower than a simple connection. However, it prevents https://en.wikipedia.org/wiki/Traffic_analysis

#### Suggested use
##### SSL
```java
// SSL Server
SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(SERVER_PORT);
SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
OutputStream outputStream = new MaskingOutputStream(clientSocket.getOutputStream(), ...);
```
```java
// SSL Client
SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(SERVER_HOST, SERVER_PORT);
InputStream inputStream = new MaskingInputStream(socket.getInputStream(), ...);
```
Then read/write to the streams as normal.
