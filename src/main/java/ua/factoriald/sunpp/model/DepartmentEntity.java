package ua.factoriald.sunpp.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;

/**
 * Клас відповідає за підрозділи
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor

@Entity
@Table(name = "DEPARTMENTS", schema = "SERVICEADMIN2")
public class DepartmentEntity {

    /**
     * Ідентифікатор підрозділу
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEPARTMENT_ID", nullable = false)
    private Long id;

    /**
     * Назва підрозділу
     */
    @Column(name="NAME", nullable = false)
    private String name;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DepartmentEntity that = (DepartmentEntity) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 586730605;
    }
}
