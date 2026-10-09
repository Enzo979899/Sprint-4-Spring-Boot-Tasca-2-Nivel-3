package cat.itacademy.s04.t02.n03.fruit.service;

import cat.itacademy.s04.t02.n03.fruit.dto.OrderItemDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.exception.OrderNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order create(OrderRequestDTO request) {

        Order order = new Order(null, request.clientName(), request.deliveryDate(), toOrderItems(request.items()));

        return orderRepository.save(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(String id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    public Order update(String id, OrderRequestDTO request) {

        Order existingOrder = findById(id);

        existingOrder.setClientName(request.clientName());
        existingOrder.setDeliveryDate(request.deliveryDate());
        existingOrder.setItems(toOrderItems(request.items()));

        return orderRepository.save(existingOrder);
    }

    public void delete(String id) {

        Order order = findById(id);

        orderRepository.delete(order);
    }

    private List<OrderItem> toOrderItems(List<OrderItemDTO> items) {

        return items.stream().map(item -> new OrderItem(item.fruitName(), item.quantityInKilos()))
                .toList();
    }

}
