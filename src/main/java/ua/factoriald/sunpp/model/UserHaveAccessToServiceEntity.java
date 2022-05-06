package ua.factoriald.sunpp.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor

@Entity
@Table(name = "USERS_HAVING_ACCESS_TO_SERVICES", schema = "SERVICEADMIN2")
public class UserHaveAccessToServiceEntity {

    /**
     * Ідентифікатор запису доступу
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCESS_ID", nullable = false)
    private Long id;

    /**
     * Користувач, якому наданий доступ
     */
    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;

    /**
     * Роль, яка надана користувачу
     */
    @OneToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private RoleEntity role;

    /**
     * Сервіс, доступ до якого наданий користувачу
     */
    @OneToOne
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private ServiceEntity service;

    /**
     * Відділення, для якого користувач отримав доступ (опціонально)
     * @null якщо не потрібно
     */
    @OneToOne
    @JoinColumn(name = "DEPARTMENT_ID")
    private DepartmentEntity department;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserHaveAccessToServiceEntity that = (UserHaveAccessToServiceEntity) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 784403030;
    }
}
