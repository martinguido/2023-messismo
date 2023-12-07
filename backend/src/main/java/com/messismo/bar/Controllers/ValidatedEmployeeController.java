package com.messismo.bar.Controllers;

import com.messismo.bar.DTOs.*;
import com.messismo.bar.Exceptions.*;
import com.messismo.bar.Services.CategoryService;
import com.messismo.bar.Services.OrderService;
import com.messismo.bar.Services.ProductService;
import com.messismo.bar.Services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/validatedEmployee")
@CrossOrigin("*")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VALIDATEDEMPLOYEE')")
public class ValidatedEmployeeController {

    private final ProductService productService;

    private final CategoryService categoryService;

    private final OrderService orderService;

    private final ReservationService reservationService;

    private final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @PostMapping("/product/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO) {
        if (productDTO.getCategory() == null || productDTO.getName() == null || productDTO.getName().isEmpty() || productDTO.getUnitPrice() == null || productDTO.getDescription() == null || productDTO.getStock() == null || productDTO.getUnitCost() == null || productDTO.getNewCategory() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing information to create a product");
        }
        if (productDTO.getUnitCost() <= 0 || productDTO.getStock() < 0 || productDTO.getUnitPrice() < 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Some values cannot be less than zero. Please check");
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(productDTO));
        } catch (CategoryNotFoundException | ExistingProductFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
    }

    @PostMapping("/filterProducts")
    public ResponseEntity<?> filterProducts(@RequestBody FilterProductDTO filterProductDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productService.filterProducts(filterProductDTO));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories());
    }

    @PostMapping("/addNewOrder")
    public ResponseEntity<?> addNewOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        if (!orderRequestDTO.getRegisteredEmployeeEmail().matches(emailRegex)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Wrong email format");
        }
        if (orderRequestDTO.getProductOrders().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Product list is empty");
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addNewOrder(orderRequestDTO));
        } catch (UserNotFoundException | ProductQuantityBelowAvailableStock e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/closeOrder")
    public ResponseEntity<?> closeOrder(@RequestBody OrderIdDTO orderIdDTO) {
        if (orderIdDTO.getOrderId() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing information to close order");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(orderService.closeOrder(orderIdDTO));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/modifyOrder")
    public ResponseEntity<?> modifyOrder(@RequestBody ModifyOrderDTO modifyOrderDTO) {
        try {
            if (modifyOrderDTO.getProductOrders().isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("New product list must not be empty");
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(orderService.modifyOrder(modifyOrderDTO));
            }
        } catch (ProductQuantityBelowAvailableStock | OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("orders/getAllOrders")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders());
    }

    @GetMapping("/getAllReservations")
    public ResponseEntity<?> getAllReservations() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.getAllReservations());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/markAsUsedReservation")
    public ResponseEntity<?> markAsUsedReservation(@RequestBody UseReservationDTO useReservationDTO) {
        if (useReservationDTO.getReservationId() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing information to use a reservation");
        } else {
            try {
                return ResponseEntity.status(HttpStatus.OK).body(reservationService.markAsUsed(useReservationDTO));
            } catch (ReservationNotFoundException | ReservationAlreadyUsedException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }

    }
}
