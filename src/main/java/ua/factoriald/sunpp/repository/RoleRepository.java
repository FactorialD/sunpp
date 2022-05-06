package ua.factoriald.sunpp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ua.factoriald.sunpp.model.RoleEntity;

/**
 * Відповідає за отримання даних з бази даних
 * Див. {@link RoleEntity}
 *
 */
@RepositoryRestResource(exported = false)
public interface RoleRepository extends JpaRepository<RoleEntity,Long> { }
