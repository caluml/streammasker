streammasker
============

https://en.wikipedia.org/wiki/Numbers_stations are interesting.
They are widely believed to be the way spies are sent their instructions.
They broadcast numbers all the time whether there are instructions in it or not. This means it's impossible to know when real data is being sent.

I wondered if it would be possible to write wrappers for Java's Input/OutputStreams that sent data at a constant rate, whether or not there was any real information to send.

This is my attempt at it.

To wrap an InputStream<br>
`MaskingInputStream maskingInputStream = new MaskingInputStream(inputStream, 8);`<br>
`new Thread(maskingInputStream).start();`

To wrap an OutputStream<br>
`MaskingOutputStream maskingOutputStream = new MaskingOutputStream(outputStream, 8, new SecureRandom(), 250, TimeUnit.MILLISECONDS);`<br>
`new Thread(maskingOutputStream).start();`

* There are a SimpleMessageServer and SimpleMessageClient to get an idea of how to use this.
* Not all methods in MaskingInputStream have been overridden yet.
* Without encryption, this is pretty pointless
