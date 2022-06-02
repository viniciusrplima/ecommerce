FROM openjdk

EXPOSE ${PORT}

COPY ./target/ecommerce-0.0.1-SNAPSHOT.jar ecommerce.jar

CMD java -jar ecommerce.jar --spring.profiles.active=prod