build:
	cd visualizer; make
	cd executor; mvn spring-boot:run

run: build
	cd executor; mvn compile
