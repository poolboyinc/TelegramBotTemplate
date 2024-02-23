package first.telegram.bot.tgbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int product_id;

    private String name;

    private int enabled;

    public int getProduct_id() {
        return product_id;
    }

    public String getName() {
        return name;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }
}
