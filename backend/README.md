## Docker

The container applies pending Alembic migrations before starting the API. Set
`DATABASE_URL` in the container environment, then rebuild and redeploy the image
to apply migrations and start the service.

Campaign and activity images are stored under `uploads/` and their URLs are
saved in `photo_url`. Docker Compose mounts this directory so uploaded files
survive API container rebuilds.