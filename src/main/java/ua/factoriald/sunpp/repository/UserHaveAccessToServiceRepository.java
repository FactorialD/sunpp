package ua.factoriald.sunpp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ua.factoriald.sunpp.model.RoleEntity;
import ua.factoriald.sunpp.model.ServiceEntity;
import ua.factoriald.sunpp.model.UserEntity;
import ua.factoriald.sunpp.model.UserHaveAccessToServiceEntity;

import java.util.List;

/**
 * Відповідає за отримання даних з бази даних
 * Див. {@link UserHaveAccessToServiceEntity}
 *
 */
@RepositoryRestResource(exported = false)
public interface UserHaveAccessToServiceRepository extends JpaRepository<UserHaveAccessToServiceEntity,Long> {


    /**
     * Повертає список записів доступу по сервісам та ролі
     * @param user користувач, записи для якого ми шукаємо
     * @return Список записів доступу
     */
    List<UserHaveAccessToServiceEntity> getAllByUserAndServiceAndRole(UserEntity user, ServiceEntity service, RoleEntity role);

    /**
     * Повертає список записів доступу по сервісам користувача
     * @param user користувач, записи для якого ми шукаємо
     * @param role потрібна роль користувача
     * @return Список записів доступу
     */
    List<UserHaveAccessToServiceEntity> getAllByUserAndRole(UserEntity user, RoleEntity role);

}
