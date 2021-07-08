run:
	mvn javafx:run

jar:
	mvn package spring-boot:repackage
	
run2:
	java -jar target/SmartHomeApp-1.0-SNAPSHOT-spring-boot.jar

clean:
	mvn clean