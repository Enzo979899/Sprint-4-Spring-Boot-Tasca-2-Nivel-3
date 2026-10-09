package cat.itacademy.s04.t02.n03.fruit.repository;

import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class OrderRepositoryIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindOrder() {

        Order order = createOrder();

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder.getId());

        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();

        assertEquals("Carlos", foundOrder.getClientName());
        assertEquals(2, foundOrder.getItems().size());
    }

    @Test
    void shouldPersistEmbeddedOrderItems() {

        Order savedOrder = orderRepository.save(createOrder());

        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();

        assertEquals("Manzana", foundOrder.getItems().get(0).getFruitName());

        assertEquals(5, foundOrder.getItems().get(0).getQuantityInKilos());

        assertEquals("Pera", foundOrder.getItems().get(1).getFruitName());
    }

    @Test
    void shouldDeleteOrder() {

        Order savedOrder = orderRepository.save(createOrder());

        orderRepository.delete(savedOrder);

        assertFalse(orderRepository.existsById(savedOrder.getId()));
    }

    private Order createOrder() {

        return new Order(null, "Carlos", LocalDate.now().plusDays(2),
                List.of(new OrderItem("Manzana", 5), new OrderItem("Pera",
                        3)));
    }

}
