package ua.factoriald.sunpp.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;

/**
 * Зберігає ролі користувачів
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor

@Entity
@Table(name = "ROLES", schema = "SERVICEADMIN2")
public class RoleEntity {

    /**
     * Ідентифікатор ролі
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID", nullable = false)
    private Long id;

    /**
     * Назва ролі
     */
    @Column(name="NAME", nullable = false)
    private String name;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoleEntity that = (RoleEntity) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 1197319562;
    }
}
