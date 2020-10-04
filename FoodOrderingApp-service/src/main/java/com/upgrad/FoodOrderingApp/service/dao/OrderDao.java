package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OrderDao {

  @PersistenceContext
  private EntityManager entityManager;

  public CouponEntity getCouponByName(String couponName) {
    final CouponEntity couponEntity;
    try {
      couponEntity = entityManager.createNamedQuery("couponByCouponName", CouponEntity.class)
          .setParameter("couponName", couponName).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }

    return couponEntity;
  }

  public List<OrderEntity> getOrdersByCustomers(CustomerEntity customerEntity) {
    try {
      return entityManager.createNamedQuery("ordersByCustomer", OrderEntity.class)
          .setParameter("customer", customerEntity).getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }


  @Transactional
  public OrderEntity createNewOrder(OrderEntity order) {
    entityManager.persist(order);
    return order;
  }

  @Transactional
  public OrderItemEntity createNewOrderItem(OrderItemEntity orderItemEntity) {
    entityManager.persist(orderItemEntity);
    return orderItemEntity;
  }

}