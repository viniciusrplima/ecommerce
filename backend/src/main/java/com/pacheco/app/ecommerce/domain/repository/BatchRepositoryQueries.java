package com.pacheco.app.ecommerce.domain.repository;

import java.math.BigInteger;

public interface BatchRepositoryQueries {

    public BigInteger countProductsInStock(Long productId);

}
