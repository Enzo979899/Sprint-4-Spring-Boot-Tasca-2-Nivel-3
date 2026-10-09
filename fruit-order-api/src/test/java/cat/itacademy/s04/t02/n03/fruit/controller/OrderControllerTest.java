package cat.itacademy.s04.t02.n03.fruit.controller;

import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.exception.OrderNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldCreateOrderAndReturnCreated() throws Exception {

        Order order = createOrder();

        when(orderService.create(any(OrderRequestDTO.class))).thenReturn(order);

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value("order-1"))
                .andExpect(jsonPath("$.clientName").value("Carlos"))
                .andExpect(jsonPath("$.items[0].fruitName").value("Manzana"))
                .andExpect(jsonPath("$.items[0].quantityInKilos").value(5));
    }

    @Test
    void shouldReturnAllOrders() throws Exception {

        when(orderService.findAll()).thenReturn(List.of(createOrder()));

        mockMvc.perform(get("/orders")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id")
                        .value("order-1")).andExpect(jsonPath("$[0].clientName")
                .value("Carlos"));
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersExist() throws Exception {

        when(orderService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/orders")).andExpect(status().isOk()).andExpect(content()
                .json("[]"));

    }

    @Test
    void shouldReturnOrderById() throws Exception {

        when(orderService.findById("order-1")).thenReturn(createOrder());

        mockMvc.perform(get("/orders/order-1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("order-1"));
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {

        when(orderService.findById("missing")).thenThrow(new OrderNotFoundException("missing"));

        mockMvc.perform(get("/orders/missing")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldUpdateOrder() throws Exception {

        when(orderService.update(eq("order-1"), any(OrderRequestDTO.class))).thenReturn(createOrder());

        mockMvc.perform(put("/orders/order-1").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value("order-1"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingOrder() throws Exception {

        when(orderService.update(eq("missing"), any(OrderRequestDTO.class)))
                .thenThrow(new OrderNotFoundException("missing"));

        mockMvc.perform(put("/orders/missing").contentType(MediaType.APPLICATION_JSON).content(validJson()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteOrder() throws Exception {

        mockMvc.perform(delete("/orders/order-1")).andExpect(status().isNoContent());

        verify(orderService).delete("order-1");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingOrder() throws Exception {

        doThrow(new OrderNotFoundException("missing")).when(orderService).delete("missing");

        mockMvc.perform(delete("/orders/missing")).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenClientNameIsBlank() throws Exception {

        String json = validJson().replace("\"Carlos\"", "\"\"");

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturnBadRequestWhenDeliveryDateIsToday() throws Exception {

        String json = validJson().replace(LocalDate.now().plusDays(2).toString(), LocalDate.now().toString());

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenItemsAreEmpty() throws Exception {

        String json = """
                {
                  "clientName": "Carlos",
                  "deliveryDate": "%s",
                  "items": []
                }
                """.formatted(LocalDate.now().plusDays(2));

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenFruitNameIsBlank() throws Exception {

        String json = validJson().replace("\"Manzana\"", "\"\"");

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenQuantityIsNegative() throws Exception {

        String json = validJson().replace("\"quantityInKilos\": 5", "\"quantityInKilos\": -2");

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldExplainWhichFieldIsInvalid() throws Exception {

        String json = validJson().replace("\"Carlos\"", "\"\"");

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message",
                        containsString("clientName")));
    }

    @Test
    void shouldReturnApiErrorWhenJsonIsMalformed() throws Exception {

        String invalidJson = """
            {
                "clientName": "Carlos",
                "deliveryDate":
            }
            """;

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed request body"));
    }

    @Test
    void shouldReturnApiErrorWhenHttpMethodIsNotAllowed() throws Exception {

        mockMvc.perform(patch("/orders/order-1")).andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                .andExpect(jsonPath("$.message").value("HTTP method not allowed"));
    }

    private Order createOrder() {

        return new Order("order-1", "Carlos", LocalDate.now().plusDays(2),
                List.of(new OrderItem("Manzana", 5)));
    }

    private String validJson() {

        return """
                {
                  "clientName": "Carlos",
                  "deliveryDate": "%s",
                  "items": [
                    {
                      "fruitName": "Manzana",
                      "quantityInKilos": 5
                    }
                  ]
                }
                """.formatted(LocalDate.now().plusDays(2));
    }

}
