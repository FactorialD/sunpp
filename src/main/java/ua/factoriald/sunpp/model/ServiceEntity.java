package ua.factoriald.sunpp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;

/**
 * Клас відповідає за сервіси
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor

@Entity
@Table(name = "SERVICES", schema = "SERVICEADMIN2")
public class ServiceEntity {

    /**
     * Ідентифікатор сервісу
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SERVICE_ID", nullable = false)
    private Long id;

    /**
     * Назва сервісу
     */
    @Column(name="NAME", nullable = false)
    private String name;

    /**
     * Власник сервісу
     */
    @JsonIgnore
    @OneToOne
    @JoinColumn(name="OWNER_USER_ID",nullable = false)
    private UserEntity ownerUser;

    /**
     * Список доступних ролей для цього сервісу
     */
    @ManyToMany
    @JoinTable(
            name = "SERVICES_HAS_AVALIABLE_ROLES",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    private List<RoleEntity> avaliableRoles;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ServiceEntity that = (ServiceEntity) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 79509190;
    }
}
