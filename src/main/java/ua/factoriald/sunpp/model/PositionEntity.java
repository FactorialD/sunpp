package ua.factoriald.sunpp.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;

/**
 * Клас відповідає за посади
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor

@Entity
@Table(name = "POSITIONS", schema = "SERVICEADMIN2")
public class PositionEntity {

    /**
     * Ідентифікатор посади
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSITION_ID", nullable = false)
    private Long id;

    /**
     * Назва посади
     */
    @Column(name="NAME", nullable = false)
    private String position;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PositionEntity that = (PositionEntity) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 1765771743;
    }
}
