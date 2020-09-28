package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    //This method returns all the restaurants according to the customer ratings
    public List<RestaurantEntity> restaurantsByRating() {
        List<RestaurantEntity> restaurantEntities = restaurantDao.getAllRestaurantsByRating();
        return restaurantEntities;
    }

    //This method checks for the restaurant search field if its empty it throws corresponding exception
    //It also returns the restaurants even if there is partial match in the restaurant in DB and the resto. mentioned in search field

    public List<RestaurantEntity> restaurantsByName(final String restaurantName) throws RestaurantNotFoundException {
        if (restaurantName.isEmpty()) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }

        List<RestaurantEntity> restaurantListByRating = restaurantDao.getAllRestaurantsByRating();
        List<RestaurantEntity> matchingRestaurantList = new ArrayList<>();

        //matching restaurants with the restaurant name mentioned in the search field and if matched populating the resto. in the matched resto. list

        for (RestaurantEntity restaurantEntity : restaurantListByRating) {
            if (restaurantEntity.getRestaurantName().toLowerCase().contains(restaurantName.toLowerCase())) {
                matchingRestaurantList.add(restaurantEntity);
            }
        }

        return matchingRestaurantList;
    }

}
