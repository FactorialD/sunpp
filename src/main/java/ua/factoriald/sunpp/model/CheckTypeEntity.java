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
@Table(name = "CHECK_TYPES", schema = "SERVICEADMIN2")
public class CheckTypeEntity {

    /**
     * Ідентифікатор типу перевірки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHECK_TYPE_ID", nullable = false)
    private Long id;

    /**
     * Тип перевірки
     * @USER_APPLICATION_RECORD запис з даними заявляча;
     * @CHECKING_RECORD запис з перевіркою власником чи адміністратором;
     * @
     * Див. {@link ua.factoriald.sunpp.model.constants.CheckTypeConstants}
     */
    @Basic
    @Column(name = "CHECK_TYPE", nullable = false)
    private String name;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CheckTypeEntity that = (CheckTypeEntity) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 73050390;
    }
}
