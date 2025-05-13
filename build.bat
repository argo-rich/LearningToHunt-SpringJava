cd web.server\src\main\resources
rmdir /S /Q static
mkdir static
cd ..\..\..\..\ClientApp
call ng.cmd build --configuration production
robocopy /S dist\client-app\browser ..\web.server\src\main\resources\static
cd ..\web.server
mvn clean install -Dmaven.test.skip=true