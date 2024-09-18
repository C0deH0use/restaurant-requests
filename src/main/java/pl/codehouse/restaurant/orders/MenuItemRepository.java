package pl.codehouse.restaurant.orders;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.awt.*;

@Repository
interface MenuItemRepository extends ReactiveCrudRepository<MenuItemEntity, Integer> {
}