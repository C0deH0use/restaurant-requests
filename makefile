run:
	docker compose build api
	docker compose up api

run-db:
	docker compose up restaurant-requests-db


destroy:
	docker stop $$(docker ps -aq) || true
	docker container stop $$(docker container ls -aq) || true
	docker container prune --force || true