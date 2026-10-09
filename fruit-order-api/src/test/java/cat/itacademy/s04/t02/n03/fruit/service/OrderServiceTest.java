package cat.itacademy.s04.t02.n03.fruit.service;

import cat.itacademy.s04.t02.n03.fruit.dto.OrderItemDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.exception.OrderNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrder() {

        OrderRequestDTO request = createRequest();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);

                    return new Order("order-1", order.getClientName(), order.getDeliveryDate(), order.getItems());
                });

        Order result = orderService.create(request);

        assertEquals("order-1", result.getId());
        assertEquals("Carlos", result.getClientName());
        assertEquals(1, result.getItems().size());
        assertEquals("Manzana", result.getItems().getFirst().getFruitName());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldReturnAllOrders() {

        Order order = createOrder();

        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> result = orderService.findAll();

        assertEquals(1, result.size());
        assertEquals("Carlos", result.getFirst().getClientName());
    }

    @Test
    void shouldReturnOrderById() {

        Order order = createOrder();

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        Order result = orderService.findById("order-1");

        assertEquals("order-1", result.getId());
        assertEquals("Carlos", result.getClientName());
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        when(orderRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findById("missing"));
    }

    @Test
    void shouldUpdateOrder() {

        Order existingOrder = createOrder();

        OrderRequestDTO request = new OrderRequestDTO("Laura", LocalDate.now().plusDays(3),
                List.of(new OrderItemDTO("Pera", 4)));

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(existingOrder));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation ->
                invocation.getArgument(0));

        Order result = orderService.update("order-1", request);

        assertEquals("Laura", result.getClientName());
        assertEquals("Pera", result.getItems().getFirst().getFruitName());
        assertEquals(4, result.getItems().getFirst().getQuantityInKilos());

        verify(orderRepository).save(existingOrder);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingOrder() {

        when(orderRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.update("missing", createRequest()));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldDeleteOrder() {

        Order order = createOrder();

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        orderService.delete("order-1");

        verify(orderRepository).delete(order);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingOrder() {

        when(orderRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.delete("missing"));

        verify(orderRepository, never()).delete(any(Order.class));
    }

    private Order createOrder() {

        return new Order("order-1", "Carlos", LocalDate.now().plusDays(2),
                List.of(new OrderItem("Manzana", 5)));
    }

    private OrderRequestDTO createRequest() {

        return new OrderRequestDTO("Carlos", LocalDate.now().plusDays(2),
                List.of(new OrderItemDTO("Manzana", 5)));
    }
}
