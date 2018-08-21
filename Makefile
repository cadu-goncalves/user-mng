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


.PHONY: build test start-testdb stop-testdb
