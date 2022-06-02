# E-commerce Test Database

## Dependencies

First install the apache utils.

```bash
sudo apt install apache2-utils
```

## Configuring Database

Create postgres database with the user, in this case `ecommerce`:

```bash
createdb ecommerce_dev -U ecommerce
```

You can populate the database using script `populate_db`. Before execute make it executable:

```bash
chmod +x populate_db
chmod +x generate_products
chmod +x generate_users
```

Then, execute the script passing database, username and password of the user:

```bash
./populate_db ecommerce_dev ecommerce ecommerce
```

## Cleaning Database

You can clear the database with the script `clear_db`. First make it executable:

```bash
chmod +x clear_db
```

Then, execute the script passing database, username and password of the user:

```bash
./clear_db ecommerce_dev ecommerce ecommerce
```