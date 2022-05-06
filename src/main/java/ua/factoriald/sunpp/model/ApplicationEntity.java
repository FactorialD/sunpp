package ua.factoriald.sunpp.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Клас зберігає основні дані заявок
 *
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor

@Entity
@Table(name = "APPLICATIONS", schema = "SERVICEADMIN2")
public class ApplicationEntity {

    /**
     * Ідентифікатор заявки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLICATION_ID", nullable = false)
    private Long id;

    /**
     * Користувач-заявляч
     */
    @OneToOne
    @JoinColumn(name = "APPLICANT_ID", referencedColumnName = "USER_ID", nullable = false)
    private UserEntity applicant;

    /**
     * Сервіс, до якого хоче отримати доступ заявляч
     */
    @OneToOne
    @JoinColumn(name = "SERVICE_FOR_ACCESS_ID", referencedColumnName = "SERVICE_ID", nullable = false)
    private ServiceEntity service;

    /**
     * Відділення, для якого користувач хоче отримати доступ (опціонально)
     * @null якщо не потрібно
     */
    @OneToOne
    @JoinColumn(name = "DEPARTMENT_ID")
    private DepartmentEntity department;

    /**
     * Дата створення заявки
     */
    @Basic
    @Column(name = "CREATION_DATE", nullable = false)
    private Timestamp creationDate;

    /**
     * Список перевірок для цієї заявки
     */
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name="APPLICATION_ID")
    @ToString.Exclude
    private List<ApplicationCheckingEntity> checkings = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ApplicationEntity that = (ApplicationEntity) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 1072691046;
    }
}
