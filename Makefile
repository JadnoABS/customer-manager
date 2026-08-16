.PHONY: help up down restart build build-no-cache ps \
        logs logs-customer logs-score \
        clean test test-customer test-score

COMPOSE = docker compose

help:
	@echo "Alvos disponíveis:"
	@echo "  make up               - Sobe todo o sistema (build + start)"
	@echo "  make down             - Para e remove os containers"
	@echo "  make restart          - down + up"
	@echo "  make build            - Builda as imagens (usando cache)"
	@echo "  make build-no-cache   - Builda as imagens sem cache"
	@echo "  make ps               - Lista status dos containers"
	@echo "  make logs             - Logs de todo o sistema"
	@echo "  make logs-customer    - Logs do customer-service + prometheus + rabbitmq"
	@echo "  make logs-score       - Logs do score-service + redis + rabbitmq"
	@echo "  make clean            - Derruba tudo e remove volumes/órfãos"
	@echo "  make test             - Roda os testes das duas aplicações"
	@echo "  make test-customer    - Roda os testes só do customer-service"
	@echo "  make test-score       - Roda os testes só do score-service"

up:
	$(COMPOSE) up -d --build

down:
	$(COMPOSE) down

restart: down up

build:
	$(COMPOSE) build

build-no-cache:
	$(COMPOSE) build --no-cache

ps:
	$(COMPOSE) ps

logs:
	$(COMPOSE) logs -f

logs-customer:
	$(COMPOSE) logs -f customer-service prometheus rabbitmq

logs-score:
	$(COMPOSE) logs -f score-service redis rabbitmq

clean:
	$(COMPOSE) down -v --remove-orphans

test: test-customer test-score

test-customer:
	cd customer-service && mvn test

test-score:
	cd score-service && mvn test
