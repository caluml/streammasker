#!/bin/bash

mvn test-compile && \
java -cp target/classes/:target/test-classes/ com.github.caluml.streammasker.SimpleMessageServer
