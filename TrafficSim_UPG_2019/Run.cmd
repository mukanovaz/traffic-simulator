if exist "compile\" rmdir /Q /S compile
mkdir compile
cd /d compile
jar xf ..\TrafficSim.jar
cd /d ../bin
xcopy /E application ..\compile\application\
cd /d ..\compile
jar cfm run.jar ..\MANIFEST.MF application\*.class TrafficSim\*.class
cd /d ..\
java -jar compile\run.jar %*
