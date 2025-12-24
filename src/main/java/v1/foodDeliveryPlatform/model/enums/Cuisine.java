package v1.foodDeliveryPlatform.model.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum Cuisine {
    ITALIAN("Итальянская"),
    JAPANESE("Японская"),
    AMERICAN("Американская"),
    MEXICAN("Мексиканская"),
    INDIAN("Индийская"),
    RUSSIAN("Русская"),
    CHINESE("Китайская"),
    FRENCH("Французская"),
    THAI("Тайская"),
    GEORGIAN("Грузинская"),
    VEGETARIAN("Вегетарианская");

    private final String name;

    Cuisine(String name) {
        this.name = name;
    }

    public static boolean isValidCuisine(String cuisineName) {
        if (cuisineName == null || cuisineName.trim().isEmpty()) {
            return false;
        }
        for (Cuisine cuisine : values()) {
            if (cuisine.name().equalsIgnoreCase(cuisineName) ||
                    cuisine.getName().equalsIgnoreCase(cuisineName)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getNames() {
        return Arrays.stream(values())
                .map(Cuisine::getName)
                .collect(Collectors.toList());
    }
}
