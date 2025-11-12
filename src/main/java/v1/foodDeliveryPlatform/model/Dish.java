package v1.foodDeliveryPlatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dish")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "image")
    @CollectionTable(name = "dish_images", joinColumns = @JoinColumn(name = "dish_id"))
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> images;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Transient
    public List<String> getImageUrls() {
        return images.stream()
                .map(path -> "/api/dishes/" + this.id + "/images/url?path=" + path)
                .collect(Collectors.toList());
    }

}
