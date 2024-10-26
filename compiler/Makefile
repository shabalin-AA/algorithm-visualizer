run-unix: build-unix
	java -ea -cp .:lib/json.jar:lib/sqlite-jdbc.jar:bin Main

build-unix: src/*.java src/**/*.java
	export JAVA_HOME=`/usr/libexec/java_home -v 17`;
	javac -cp .:lib/json.jar:lib/sqlite-jdbs.jar src/**/**/*.java src/**/*.java src/*.java -d bin

run-win: build-win
	java -ea -cp .;lib/json.jar;bin Main

build-win: src/**/*.java src/*.java
	javac -cp .;lib/json.jar;bin src/**/*.java src/*.java -d bin
