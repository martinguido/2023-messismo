package com.messismo.bar.Controllers;

import com.messismo.bar.DTOs.*;
import com.messismo.bar.Services.CategoryService;
import com.messismo.bar.Services.OrderService;
import com.messismo.bar.Services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/validatedEmployee")
@CrossOrigin("*")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VALIDATEDEMPLOYEE')")
public class ValidatedEmployeeControlller {

    private final ProductService productService;

    private final CategoryService categoryService;

    private final OrderService orderService;


    @PostMapping("/product/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO) {
        return productService.addProduct(productDTO);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<?> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/filterProducts")
    public ResponseEntity<?> filterProducts(@RequestBody FilterProductDTO filterProductDTO) {
        return productService.filterProducts(filterProductDTO);
    }

    @PostMapping("/addNewOrder")
    public ResponseEntity<?> addNewOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        return orderService.addNewOrder(orderRequestDTO);
    }

    @PostMapping("/closeOrder")
    public ResponseEntity<?> closeOrder(@RequestBody OrderIdDTO orderIdDTO) {
        return orderService.closeOrder(orderIdDTO);
    }

    @PostMapping("/modifyOrder")
    public ResponseEntity<?> modifyOrder(@RequestBody ModifyOrderDTO modifyOrderDTO) {
        return orderService.modifyOrder(modifyOrderDTO);
    }

    @GetMapping("orders/getAllOrders")
    public ResponseEntity<?> getAllOrders(){
        return orderService.getAllOrders();
    }

}