package com.pacheco.app.ecommerce.infrastructure.repository;

import com.pacheco.app.ecommerce.domain.model.Batch;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.domain.model.PurchaseItem;
import com.pacheco.app.ecommerce.domain.repository.BatchRepositoryQueries;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.Optional;

@Repository
public class BatchRepositoryImpl implements BatchRepositoryQueries {

    @PersistenceContext
    private EntityManager manager;

    /*
    * Count products registered in batch and subtract the products
    * that was added to users carts and that was purchased but not
    * delivered yet
    * */
    @Override
    public BigInteger countProductsInStock(Long productId) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();

        CriteriaQuery<BigInteger> stockQuery = cb.createQuery(BigInteger.class);
        Root<Batch> batch = stockQuery.from(Batch.class);
        stockQuery.select(cb.sum(batch.get("quantity")));
        stockQuery.where(cb.equal(batch.get("product").get("id"), productId));

        CriteriaQuery<BigInteger> cartQuery = cb.createQuery(BigInteger.class);
        Root<CartItem> cartItem = cartQuery.from(CartItem.class);
        cartQuery.select(cb.sum(cartItem.get("quantity")));
        cartQuery.where(cb.and(
                cb.equal(cartItem.get("product").get("id"), productId),
                cb.equal(cartItem.get("reserved"), Boolean.TRUE)));

        CriteriaQuery<BigInteger> purchaseQuery = cb.createQuery(BigInteger.class);
        Root<PurchaseItem> purchaseItem = purchaseQuery.from(PurchaseItem.class);
        purchaseQuery.select(cb.sum(purchaseItem.get("quantity")));
        purchaseQuery.where(cb.equal(purchaseItem.get("product").get("id"), productId));

        BigInteger stockSum = Optional.ofNullable(
                    manager.createQuery(stockQuery).getSingleResult())
                .orElse(BigInteger.ZERO);

        BigInteger cartSum = Optional.ofNullable(
                    manager.createQuery(cartQuery).getSingleResult())
                .orElse(BigInteger.ZERO);

        BigInteger purchaseSum = Optional.ofNullable(
                    manager.createQuery(purchaseQuery).getSingleResult())
                .orElse(BigInteger.ZERO);

        return stockSum.subtract(cartSum).subtract(purchaseSum);
    }
}
