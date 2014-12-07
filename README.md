streammasker
============

https://en.wikipedia.org/wiki/Numbers_stations are interesting.
They are widely believed to be the way spies are sent their instructions.
They broadcast numbers all the time whether there are instructions in it or not. This means it's impossible to know when real data is being sent.

This is my attempt at it writing wrappers for Java's Input/OutputStreams that essentially hide whether any data is being sent or not. They work by sending dummy or real data at regular intervals.

To wrap an OutputStream (sending 10 bytes (8 + 2) every 250 milliseconds)<br>
`MaskingOutputStream maskingOutputStream = new MaskingOutputStream(outputStream, 8, new SecureRandom(), 250, TimeUnit.MILLISECONDS);`<br>
`new Thread(maskingOutputStream).start();`

To wrap an InputStream<br>
`MaskingInputStream maskingInputStream = new MaskingInputStream(inputStream, 8);`<br>
`new Thread(maskingInputStream).start();`

Notes
* There are a SimpleMessageServer and SimpleMessageClient to get an idea of how to use this.
* Not all methods in MaskingInputStream have been overridden yet.
* Without encryption, this is pretty pointless, as it's trivial to see the data being sent/not being sent.
* This effectively limits and uses/wastes a fixed amount of bandwidth
* It sends random data to mitigate against https://en.wikipedia.org/wiki/Known-plaintext_attack. (I don't know if it's actually necessary these days with modern ciphers like AES)
