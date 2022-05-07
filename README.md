# E-commerce

Sample e-commerce of generic products.

## Running

First, copy `.env.example` to `.env` and fill the variables 
`APP_EMAIL_MAIN_EMAIL`, `APP_EMAIL_NOTIFY_EMAIL`, `APP_EMAIL_MAIN_PASSWORD`, 
`APP_S3_IMAGE_BUCKET_NAME`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`. 
See [this section](#configuring).

```
cp .env.example .env
```

Then, package the project with the command below.

```
mvn clean package
```

Finally, execute system with docker-compose.

```
docker-compose up -d --build
```

To stop the system you should execute the following command.

```
docker-compose down
```

## Configuring

The system uses the following environment variables:

| Variable | Use |
|------|------|
| PORT | Port used by the rest API. |
| DATABASE_URL | Postgres Database URL. |
| DATABASE_USERNAME | Postgres Database Username. |
| ADMIN_USERNAME | Username for system admin account |
| ADMIN_PASSWORD | Password for system admin account |
| APPLICATION_JWT_SECRET | Secret used to proccess JWT tokens |
| APPLICATION_JWT_EXPIRE_TIME | JWT token expiration time. |
| APP_EMAIL_MAIN_EMAIL | Email used by system to send emails. |
| APP_EMAIL_MAIN_PASSWORD | Main email password. |
| APP_EMAIL_NOTIFY_EMAIL | Email used by system to notify admin about an event. |
| APP_S3_IMAGE_BUCKET_NAME | Bucket name of the aws S3 to store images. |
| AWS_ACCESS_KEY_ID | Key ID to access aws services. |
| AWS_SECRET_ACCESS_KEY | Secret Key to access aws services. |


## Testing 

You must configure the postgres user with the superuser 
privileges to properly run the tests.

Create user with this command: 

```sql
CREATE USER ecommerce WITH PASSWORD 'ecommerce' SUPERUSER;
```

The superuser is necessary because the cleaning of database 
need to disable constraints.

After this, create the databases for the system. Create the
test database with `test` suffix to enable for the database
cleaner to clear all tables ensuring that it is not the 
production database.

```sql
CREATE DATABASE ecommerce;
CREATE DATABASE ecommerce_test;
```

Then, ensure all privileges to user on all used databases.

```sql
GRANT ALL PRIVILEGES 
    ON DATABASE ecommerce, ecommerce_test 
    TO ecommerce;
```

The test configuration can be customized changing the file
`src/test/resources/application-test.properties`.

Finally, run the command bellow to execute the integration tests.

```bash
mvn verify
```
