run:
	docker compose build api
	docker compose up api

destroy:
	docker stop $$(docker ps -aq) || true
	docker container stop $$(docker container ls -aq) || true
	docker container prune --force || true