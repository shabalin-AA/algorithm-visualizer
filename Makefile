build:
	cd visualizer; make
	cd compiler; make build-unix

run: build
	cd compiler; make run-unix
