# Mock API - Users & Folders

REST API served by NGINX in Docker, exposing user and folder data as JSON.

## Running the service

```bash
docker compose up -d
```

The service is available at `http://localhost:8080`.

To stop it:

```bash
docker compose down
```

## Regenerating data

```bash
python3 generate_data.py
```

This produces 10 users with 5 to 15 folders each in the `data/` directory.

## Endpoints

### GET /users

Returns the list of user email addresses.

```bash
curl http://localhost:8080/users
```

```json
["john@linagora.com", "alice@linagora.com", "..."]
```

### GET /users/{email}/folders

Returns the folders for a given user.

```bash
curl http://localhost:8080/users/john@linagora.com/folders
```

```json
[
  {"id": "9d68e13e-fa7e-476e-b4d0-a80aec399be2", "name": "Trash"},
  {"id": "ef17f006-a454-46ee-8ab8-8fa13629797c", "name": "Inbox"}
]
```

### GET /folders

Returns all folders across all users.

```bash
curl http://localhost:8080/folders
```

```json
[
  {"id": "9d68e13e-fa7e-476e-b4d0-a80aec399be2", "user": "john@linagora.com", "name": "Trash"},
  {"id": "ef17f006-a454-46ee-8ab8-8fa13629797c", "user": "john@linagora.com", "name": "Inbox"}
]
```
