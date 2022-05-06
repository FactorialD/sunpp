package ua.factoriald.sunpp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ua.factoriald.sunpp.model.WorkerEntity;


/**
 * Відповідає за отримання даних з бази даних
 * Див. {@link WorkerEntity}
 *
 */
@RepositoryRestResource(exported = false)
public interface WorkerRepository extends JpaRepository<WorkerEntity,Long> { }
