package cat.itacademy.s04.t02.n03.fruit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record OrderItemDTO(@NotBlank String fruitName, @Positive int quantityInKilos) {

}
