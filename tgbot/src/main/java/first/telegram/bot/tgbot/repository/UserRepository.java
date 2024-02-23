package first.telegram.bot.tgbot.repository;

import first.telegram.bot.tgbot.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    @Query("SELECT u FROM users u WHERE u.chat_id = ?1")
    Optional<User> findByChatId(long chatId);

}
