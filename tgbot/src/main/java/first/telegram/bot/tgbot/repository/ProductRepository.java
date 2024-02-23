package first.telegram.bot.tgbot.repository;

import first.telegram.bot.tgbot.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    @Query("SELECT p.name FROM products p")
    List<String> findAllNames();
}
