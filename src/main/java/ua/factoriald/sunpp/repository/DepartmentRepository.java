package ua.factoriald.sunpp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ua.factoriald.sunpp.model.DepartmentEntity;

/**
 * Відповідає за отримання даних з бази даних
 * Див. {@link DepartmentEntity}
 *
 */
@RepositoryRestResource(exported = false)
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> { }
