package ua.factoriald.sunpp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ua.factoriald.sunpp.model.UserEntity;

/**
 * Відповідає за отримання даних з бази даних
 * Див. {@link UserEntity}
 *
 */
@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    /**
     * Повертає користувача за логіном
     * @param login Логін користувача
     * @return Користувач
     */
    UserEntity findByLogin(String login);

}
