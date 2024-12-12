build:
	cd visualizer; npm install; npm run build
	cd executor; mvn compile

run:
	cd executor; mvn spring-boot:run &
	cd visualizer; npm start &

war: static
	cd executor; mvn clean package
	cp executor/target/executor.war /usr/local/Cellar/tomcat/10.1.33/libexec/webapps

static:
	cd visualizer; npm run build
	cp -r visualizer/build/* executor/src/main/resources/static/
	cd executor; make run

stop:
	killall node
	killall java
