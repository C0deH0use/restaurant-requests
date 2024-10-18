run:
	docker compose build api
	docker compose up api

run-dependencies:
	docker compose up --force-recreate --renew-anon-volumes

run-db:
	docker compose up --force-recreate --renew-anon-volumes restaurant-requests-db


destroy:
	docker stop $$(docker ps -aq) || true
	docker container stop $$(docker container ls -aq) || true
	docker container prune --force || true
