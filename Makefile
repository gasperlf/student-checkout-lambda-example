ENV_EXAMPLE=test-example

.PHONY: clean
clean:
	rm -rf .aws-sam build

.PHONY: build
build: clean
	sam build

.PHONY: run
run: build
	docker-compose up -d
	sam local start-lambda --docker-network docker-network-${ENV_EXAMPLE} --env-vars env.json

.PHONY: stop
stop:
	docker-compose down
