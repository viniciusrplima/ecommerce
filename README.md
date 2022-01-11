# E-commerce

Sample e-commerce of generic products.

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
