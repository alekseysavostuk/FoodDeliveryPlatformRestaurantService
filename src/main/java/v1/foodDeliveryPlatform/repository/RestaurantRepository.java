package v1.foodDeliveryPlatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import v1.foodDeliveryPlatform.model.Restaurant;

import java.util.List;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    @Query(value = "SELECT * FROM restaurant WHERE LOWER(cuisine) = LOWER(:cuisine)", nativeQuery = true)
    List<Restaurant> findAllByCuisine(@Param("cuisine") String cuisine);
}
