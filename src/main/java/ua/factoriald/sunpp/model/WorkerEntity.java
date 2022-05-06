package ua.factoriald.sunpp.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Клас відповідає за дані користувача
 */
@Getter
@Setter
@RequiredArgsConstructor

@Entity
@Table(name = "WORKERS", schema = "SERVICEADMIN2")
public class WorkerEntity {

    /**
     * Ідентифікатор робітника
     */
    @Column(name = "WORKER_ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Повне ім'я робітника
     */
    @Column(name="FULLNAME", nullable = false)
    private String fullname;

    /**
     * Підрозділ, до якого відноситься робітник
     */
    @OneToOne
    @JoinColumn(name="DEPARTMENT_ID", nullable = false)
    private DepartmentEntity department;

    /**
     * Посада робітника
     */
    @OneToOne
    @JoinColumn(name="POSITION_ID", nullable = false)
    private PositionEntity position;

    /**
     * Список користвачів для цього робітника
     */
    @OneToMany
    @JoinColumn(name = "WORKER_ID")
    private Set<UserEntity> users = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        WorkerEntity that = (WorkerEntity) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 1177951510;
    }

    @Override
    public String toString() {
        return "WorkerEntity{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", department=" + department +
                ", position=" + position +
                ", users=" + users +
                '}';
    }
}
