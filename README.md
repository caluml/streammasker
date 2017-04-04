streammasker
============

https://en.wikipedia.org/wiki/Numbers_stations are interesting.
They are widely believed to be the way spies are sent their instructions.
They broadcast numbers all the time whether there are instructions in it or not. This means it's impossible to know when real data is being sent, because data is being sent all the time.

### What is this?
This is my attempt at writing wrappers for Java's Input/OutputStreams that essentially prevent someone viewing network traffic from knowing whether any information is being sent or not.

### How to use
Both wrappers need to run in threads to be able to send fake data when there is no real data to be sent.<br>
To wrap an OutputStream (sending 10 bytes (8 + 2) every 250 milliseconds)
```
MaskingOutputStream maskingOutputStream = new MaskingOutputStream(outputStream, 8, new SecureRandom(), 250, TimeUnit.MILLISECONDS);
new Thread(maskingOutputStream).start();
```

To wrap an InputStream
```
MaskingInputStream maskingInputStream = new MaskingInputStream(inputStream, 8);
new Thread(maskingInputStream).start();
```

### Notes
* There are a SimpleMessageServer and SimpleMessageClient to get an idea of how to use this. `tcpdump -X -npi any tcp dst port 7733` will show you the traffic. There are a couple of scripts to run the example server and client.
* Not all methods in MaskingInputStream have been overridden yet.
* Without encryption, this is pretty pointless, as it's trivial to see the data being sent/not being sent. Encryption isn't part of this library though.
* streammasker effectively limits and uses/wastes a fixed amount of bandwidth.
* It sends random data to mitigate against https://en.wikipedia.org/wiki/Known-plaintext_attack. (I don't know if it's actually necessary these days with modern ciphers like AES)

#### But isn't this a very inefficient way of transmitting data?
Yes.
