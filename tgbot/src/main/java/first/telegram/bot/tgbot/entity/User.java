package first.telegram.bot.tgbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;

    private String store;
    private String username;
    private String Type;
    private String Name;
    private String Location;
    private long chat_id;

    public long getUser_id() {
        return user_id;
    }

    public String getStore() {
        return store;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return Type;
    }

    public String getName() {
        return Name;
    }

    public String getLocation() {
        return Location;
    }

    public long getChat_id() { return chat_id; }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setChat_id(long chat_id) {
        this.chat_id = chat_id;
    }
}
