package com.upgrad.FoodOrderingApp.service.businness;


import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

  @Autowired
  private RestaurantDao restaurantDao;

  @Autowired
  private CategoryDao categoryDao;

  @Autowired
  private OrderItemDao orderItemDao;

  @Autowired
  private ItemDao itemDao;

  @Autowired
  private OrderDao orderDao;


  /**
   * This method gets Items for a given category in a restaurant
   *
   * @param restaurantId Restaurant whose items are to be queried, categoryUuid Category to be
   *                     queried.
   * @return List of ItemEntity
   */
  public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantId, String categoryId) {
    RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(restaurantId);
    CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryId);
    List<ItemEntity> restaurantItemList = new ArrayList<>();

    for (ItemEntity restaurantItem : restaurantEntity.getItems()) {
      for (ItemEntity categoryItem : categoryEntity.getItems()) {
        if (restaurantItem.getUuid().equals(categoryItem.getUuid())) {
          restaurantItemList.add(restaurantItem);
        }
      }
    }
    restaurantItemList.sort(Comparator.comparing(ItemEntity::getItemName));
    return restaurantItemList;
  }

  public List<OrderItemEntity> getItemsByOrder(OrderEntity orderEntity) {
    return orderItemDao.getItemsByOrder(orderEntity);
  }

  //Method called to get the ItemEntity Instance by passing the UUID
  public ItemEntity getItemByUUID(String itemId) throws ItemNotFoundException {
    ItemEntity itemEntity = itemDao.getItemById(itemId);
    if (itemEntity == null) {
      throw new ItemNotFoundException("INF-003", "No item by this id exist");
    } else {
      return itemEntity;
    }
  }

  /**
   * This method gets top five popular items of a restaurant.
   *
   * @param restaurantEntity Restaurant whose top five items are to be queried.
   * @return top five items
   */
  public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
    List<ItemEntity> itemEntityList = new ArrayList<ItemEntity>();
    for (OrderEntity orderEntity : orderDao.getOrdersByRestaurant(restaurantEntity)) {
      for (OrderItemEntity orderItemEntity : orderItemDao.getItemsByOrder(orderEntity)) {
        itemEntityList.add(orderItemEntity.getItemId());
      }
    }

    // counting all items with map
    Map<String, Integer> map = new HashMap<String, Integer>();
    for (ItemEntity itemEntity : itemEntityList) {
      Integer count = map.get(itemEntity.getUuid());
      map.put(itemEntity.getUuid(), (count == null) ? 1 : count + 1);
    }

    Map<String, Integer> map1 = new TreeMap<String, Integer>(map);
    List<ItemEntity> sortedItemEntityList = new ArrayList<ItemEntity>();
    for (Map.Entry<String, Integer> entry : map1.entrySet()) {
      sortedItemEntityList.add(itemDao.getItemByUUID(entry.getKey()));
    }
    Collections.reverse(sortedItemEntityList);

    return sortedItemEntityList;
  }


}
