package ua.factoriald.sunpp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Клас зберігає записи етапів перевірки заявок
 *
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor

@Entity
@Table(name = "APPLICATION_CHECKING", schema = "SERVICEADMIN2")
public class ApplicationCheckingEntity {

    /**
     * Ідентифікатор перевірки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHECK_ID", nullable = false)
    private Long id;

    /**
     * Показує, чи переглянута заявка окремим користувачем
     * @null заявка не переглянута
     * @true заявка підтверджена цим користувачем
     * @false заявка відхилена цим користувачем
     */
    @Basic
    @Column(name = "CHECK_YES_NO_NULL")
    private Boolean checkYesNoNull;

    /**
     * Дата перевірки
     * @null якщо заявка ще не переглядалася цим коритсувачем
     */
    @Basic
    @Column(name = "CHECKING_DATE")
    private Timestamp checkingDate;

    /**
     * Коментар користувача (опціонально)
     */
    @Basic
    @Column(name = "NOTE")
    private String note;

    /**
     * Заявка, до якої відноситься перевірка
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private ApplicationEntity application;

    /**
     * Тип перевірки
     * Див. {@link CheckTypeEntity}
     */
    @OneToOne
    @JoinColumn(name = "CHECK_TYPE_ID", nullable = false)
    private CheckTypeEntity checkType;

    /**
     * Роль користувача
     * Якщо @checkType = USER_APPLICATION_RECORD, то це потрібна роль заявляча
     * Якщо @checkType = CHECKING_RECORD, то це потрібна роль перевіряючого
     */
    @OneToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private RoleEntity role;

    /**
     * Користувач
     * Якщо @checkType = USER_APPLICATION_RECORD, то це заявляч
     * Якщо @checkType = CHECKING_RECORD, то це перевіряючий
     *
     * @null якщо заявка ще не перевірялась
     */
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ApplicationCheckingEntity that = (ApplicationCheckingEntity) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 907465219;
    }
}
