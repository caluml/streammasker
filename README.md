streammasker
============

https://en.wikipedia.org/wiki/Numbers_station s are interesting.
They are widely believed to be the way spies are sent their instructions.
They broadcast numbers all the time whether there are instructions in it or not. This means it's impossible to know when real data is being sent.

I wondered if it would be possible to write wrappers for Java's Input/OutputStreams that sent data at a constant rate, whether or not there was any information to send.

This is my attempt at it.

To wrap an InputStream
`MaskingInputStream maskingInputStream = new MaskingInputStream(inputStream, 8);
new Thread(maskingInputStream).start();`

To wrap an OutputStream
`MaskingOutputStream maskingOutputStream = new MaskingOutputStream(outputStream, 8, new SecureRandom(), 250, TimeUnit.MILLISECONDS);
new Thread(maskingOutputStream).start();`

There are a SimpleMessageServer and SimpleMessageClient to get an idea of how to use this.

Not all methods in MaskingInputStream have been overridden yet.