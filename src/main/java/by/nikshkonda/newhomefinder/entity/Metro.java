package by.nikshkonda.newhomefinder.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Metro")
@Table(name = "metro", schema = "public")
public class Metro {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metro_generator")
    @SequenceGenerator(name = "metro_generator", sequenceName = "metro_seq")
    private Long id;

    private String metro;
    private Double distance;

    public Metro(String metro, Double distance) {
        this.metro = metro;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Metro metro1 = (Metro) o;

        return new EqualsBuilder()
                .append(metro, metro1.metro)
                .append(distance, metro1.distance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(metro)
                .append(distance)
                .toHashCode();
    }

    @Override
    public String toString() {
        return String.format("%s %.2f км; ", metro, distance);
    }
}
