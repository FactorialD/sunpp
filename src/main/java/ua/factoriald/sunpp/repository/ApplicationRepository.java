package ua.factoriald.sunpp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ua.factoriald.sunpp.model.ApplicationEntity;
import ua.factoriald.sunpp.model.ServiceEntity;
import ua.factoriald.sunpp.model.UserEntity;

import java.util.List;

/**
 * Відповідає за отримання даних з бази даних
 * Див. {@link ApplicationEntity}
 *
 */
@RepositoryRestResource(exported = false)
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

    /**
     * Повертає всі заявки, що належать сервісу
     * @param service сервіс, по якому вибираються заявки
     * @return Список заявок
     */
    List<ApplicationEntity> getAllByService(ServiceEntity service);

    /**
     * Повертає всі заявки, що належать сервісу зі списку
     * @param services список сервісів, по яким вибираються заявки
     * @return Список заявок
     */
    List<ApplicationEntity> getAllByServiceIn(List<ServiceEntity> services);

    /**
     * Повертає заявки по їх заявлячу
     * @param user Заявляч
     * @return Список заявок
     */
    List<ApplicationEntity> getAllByApplicant(UserEntity user);

}
