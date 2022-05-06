package ua.factoriald.sunpp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ua.factoriald.sunpp.model.PositionEntity;

/**
 * Відповідає за отримання даних з бази даних
 * Див. {@link PositionEntity}
 *
 */
@RepositoryRestResource(exported = false)
public interface PositionRepository extends JpaRepository<PositionEntity,Long> { }
