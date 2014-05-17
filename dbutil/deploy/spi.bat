echo off
"C:\Program Files (x86)\Java\jre7\bin\java" -Djavax.net.ssl.trustStore=cacerts -classpath .;spi.jar;Tidy.jar -Xmx256m com.spi.Main
pause
