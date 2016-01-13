@echo off
title Test Server
"C:\Program Files\Java\jdk1.8.0_65\bin\java.exe" -Xmx1024m -cp bin;deps/jna.jar;deps/guava-18.0.jar;deps/json-simple-1.1.1.jar;deps/json-lib-2.4-jdk15.jar;deps/oshi-core.jar;deps/platform.jar;deps/poi.jar;deps/xstream.jar;deps/Motivote-server.jar;deps/gson-2.2.4.jar;deps/mchange-commons-java-0.2.8.jar;deps/c3p0-0.9.5-pre10.jar;deps/netty.jar;deps/mysql.jar;deps/slf4j.jar;deps/slf4j-nop.jar;deps/commons-io-2.4.jar;deps/commons-lang3-3.3.2.jar;deps/jython.jar;log4j-1.2.15.jar; ab.Server
pause