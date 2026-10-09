package cat.itacademy.s04.t02.n03.fruit.controller;

import cat.itacademy.s04.t02.n03.fruit.dto.OrderRequestDTO;
import cat.itacademy.s04.t02.n03.fruit.dto.OrderResponseDTO;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody OrderRequestDTO request) {

        Order savedOrder = orderService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponseDTO.from(savedOrder));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> findAll() {

        List<OrderResponseDTO> orders = orderService.findAll().stream().map(OrderResponseDTO::from).toList();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable String id) {

        Order order = orderService.findById(id);

        return ResponseEntity.ok(OrderResponseDTO.from(order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> update(@PathVariable String id,
                                                   @Valid @RequestBody OrderRequestDTO request) {

        Order updatedOrder = orderService.update(id, request);

        return ResponseEntity.ok(OrderResponseDTO.from(updatedOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {

        orderService.delete(id);

        return ResponseEntity.noContent().build();
    }
}