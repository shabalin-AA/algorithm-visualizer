run-unix: build-unix
	java -ea -cp .:lib/json.jar:lib/sqlite-jdbc.jar:bin Main

build-unix: src/*.java src/**/*.java
	javac -cp .:lib/json.jar:lib/sqlite-jdbc.jar:bin -sourcepath src src/*.java src/**/*.java -d bin

run-win: build-win
	java -ea -cp .;lib/json.jar;bin Main

build-win: src/**/*.java src/*.java
	javac -cp .;lib/json.jar;bin -sourcepath src src/**/*.java src/*.java -d bin
