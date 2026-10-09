package cat.itacademy.s04.t02.n03.fruit.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldContainOrderItems() {

        List<OrderItem> items = List.of(new OrderItem("Manzana", 5),
                new OrderItem("Pera", 3));

        Order order = new Order(null, "Carlos", LocalDate.now().plusDays(2), items);

        assertEquals(2, order.getItems().size());
        assertSame(items, order.getItems());
        assertEquals("Manzana", order.getItems().get(0).getFruitName());
        assertEquals(3, order.getItems().get(1).getQuantityInKilos());
    }
}
