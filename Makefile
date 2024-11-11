build:
	cd visualizer; npm run build
	cd executor; mvn compile

run: 
	cd executor; mvn spring-boot:run &
	cd visualizer; npm start &
