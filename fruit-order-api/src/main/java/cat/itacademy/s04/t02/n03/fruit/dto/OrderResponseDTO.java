package cat.itacademy.s04.t02.n03.fruit.dto;

import cat.itacademy.s04.t02.n03.fruit.model.Order;

import java.time.LocalDate;
import java.util.List;

public record OrderResponseDTO(String id, String clientName, LocalDate deliveryDate, List<OrderItemDTO> items) {

    public static OrderResponseDTO from(Order order) {

        List<OrderItemDTO> items = order.getItems().stream().map(item -> new OrderItemDTO(item.getFruitName(),
                        item.getQuantityInKilos())).toList();

        return new OrderResponseDTO(order.getId(), order.getClientName(), order.getDeliveryDate(), items);
    }
}
