run: build
	cd bin; java -ea -cp .:../lib/json.jar Main

build: src/*.java
	javac -cp lib/*.jar src/*.java -d bin
