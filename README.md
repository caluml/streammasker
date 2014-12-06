streammasker
============

https://en.wikipedia.org/wiki/Numbers_stations are interesting.
They are widely believed to be the way spies are sent their instructions.
They broadcast numbers all the time whether there are instructions in it or not. This means it's impossible to know when real data is being sent.

I wondered if it would be possible to write wrappers for Java's Input/OutputStreams that sent data at a constant rate, whether or not there was any real information to send.

This is my attempt at it.

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
