package ua.factoriald.sunpp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ua.factoriald.sunpp.model.CheckTypeEntity;

/**
 * Відповідає за отримання даних з бази даних
 * Див. {@link CheckTypeEntity}
 *
 */
@RepositoryRestResource(exported = false)
public interface CheckTypeRepository extends JpaRepository<CheckTypeEntity, Long> { }
