package ua.factoriald.sunpp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor

@Entity
@Table(name = "USERS", schema = "SERVICEADMIN2")
public class UserEntity {

    /**
     * Ідентифікатор користувача
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long id;

    /**
     * Логін користувача
     */
    @Basic
    @Column(name = "LOGIN", nullable = false)
    private String login;

    /**
     * Працівник, який використовує цього користувача
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "WORKER_ID", nullable = false)
    private WorkerEntity workerEntity;

    /**
     * Список записів доступу для цього користувача
     */
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name="USER_ID")
    @ToString.Exclude
    private List<UserHaveAccessToServiceEntity> roles = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserEntity that = (UserEntity) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 1838525018;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", workerEntity=" + workerEntity.getId() +
                '}';
    }
}
