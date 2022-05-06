package ua.factoriald.sunpp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ua.factoriald.sunpp.model.ServiceEntity;
import ua.factoriald.sunpp.model.UserEntity;

import java.util.List;

/**
 * Відповідає за отримання даних з бази даних
 * Див. {@link ServiceEntity}
 *
 */
@RepositoryRestResource(exported = false)
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    /**
     * Повертає сервіси по власнику
     * @param user Власник сервісу
     * @return Список сервісів
     */
    List<ServiceEntity> getAllByOwnerUser(UserEntity user);

}
