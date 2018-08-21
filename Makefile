.PHONY: test

test:
	mvn clean test


.PHONY: start-db

start-db:
	docker-compose up -d

