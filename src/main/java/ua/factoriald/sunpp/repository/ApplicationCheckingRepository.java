package ua.factoriald.sunpp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ua.factoriald.sunpp.model.ApplicationCheckingEntity;

/**
 * Відповідає за отримання даних з бази даних
 * Див. {@link ApplicationCheckingEntity}
 *
 */
@RepositoryRestResource(exported = false)
public interface ApplicationCheckingRepository extends JpaRepository<ApplicationCheckingEntity,Long> { }
