package by.nikshkonda.newhomefinder.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Apartment")
@Table(name = "apartment", schema = "public")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apartment_generator")
    @SequenceGenerator(name="apartment_generator", sequenceName = "apartment_seq")
    private Long id;

    private Long resourceId;
    private Resource resource;

    private String url;

    private Double latitude;
    private Double longitude;
    private String address;
    private String userAddress;

    private Double priceUSD;
    private Double priceDiff;

    private Integer numberOfRoom;
    private Integer floor;
    private Integer numberOfFloor;

    private Float totalArea;
    private Float livingArea;
    private Float kitchenArea;

    private String sellerType;

    private Integer year;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Metro> metroDist;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime created;
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updated;

    public void addMetro(Metro metro) {
        if (metroDist == null) {
            metroDist = new HashSet<>();
        }
        metroDist.add(metro);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\uD83C\uDFE0 Квартира ").append(id).append('\n');
        sb.append(address).append('\n');
        sb.append("\uD83D\uDCB5 Цена ").append(priceUSD).append(" USD");
        if (priceDiff != null && priceDiff > 10D) {
            sb.append(" (").append(priceDiff).append(")");
        }
        sb.append("\nКомнат ").append(numberOfRoom);
        sb.append(" Этаж ").append(floor).append('/').append(numberOfFloor).append('\n');
        sb.append("Площадь ").append(totalArea).append('/').append(livingArea).append('/').append(kitchenArea).append('\n');
        sb.append("\uD83D\uDE87 ");
        if (!CollectionUtils.isEmpty(metroDist)) metroDist.forEach(sb::append);
        else if (Resource.TS.equals(resource) && latitude == null && longitude == null)sb.append("НЕ СМОГ ВЫЧИСЛИТЬ МЕТРО ((((");
        sb.append('\n').append(url);
        return sb.toString();
    }

    public enum Resource {
        ONLINER, REALT, TS, ETAGI
    }
}
