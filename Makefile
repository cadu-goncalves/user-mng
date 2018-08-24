run-local:
	mvn clean package -Dmaven.test.skip=true; \
	make stop-testdb; \
	make start-testdb; \
	mvn spring-boot:run

build:
	make start-testdb; \
	mvn clean package; \
	make stop-testdb

test:
	make start-testdb; \
	mvn clean test; \
	make stop-testdb

start-testdb:
	docker-compose up -d

stop-testdb:
	docker-compose rm -s --force


.PHONY: run-local build test start-testdb stop-testdb
