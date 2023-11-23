package com.messismo.bar.Controllers;

import com.messismo.bar.DTOs.*;
import com.messismo.bar.Exceptions.*;
import com.messismo.bar.Services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manager")
@CrossOrigin("*")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ManagerController {

    private final ProductService productService;

    private final UserService userService;

    private final CategoryService categoryService;

    private final DashboardService dashboardService;

    private final GoalService goalService;

    @PutMapping("/product/updatePrice")
    public ResponseEntity<?> updateProductPrice(@RequestBody ProductPriceDTO productPriceDTO) {
        if (productPriceDTO.getUnitPrice() == null || productPriceDTO.getProductId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Missing data to modify product price");
        }
        if (productPriceDTO.getUnitPrice() <= 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Product price CANNOT be less than 0.");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productService.modifyProductPrice(productPriceDTO));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/product/updateCost")
    public ResponseEntity<?> updateProductCost(@RequestBody ProductPriceDTO productPriceDTO) {
        if (productPriceDTO.getUnitPrice() == null || productPriceDTO.getProductId() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Missing data to modify product cost");
        }
        if (productPriceDTO.getUnitPrice() <= 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Product cost CANNOT be less than 0.");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productService.modifyProductCost(productPriceDTO));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/product/modifyProductStock")
    public ResponseEntity<?> modifyProductStock(@RequestBody ProductStockDTO productStockDTO) {
        if (productStockDTO.getModifyStock() == null || productStockDTO.getProductId() == null || productStockDTO.getOperation() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing data to add product stock");
        }
        if (productStockDTO.getModifyStock() < 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Stock quantity cannot be less than 0");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productService.modifyProductStock(productStockDTO));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @DeleteMapping("/product/deleteProduct/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(productId));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAllEmployees")
    public ResponseEntity<?> getAllEmployees() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllEmployees());
    }

    @PutMapping("/validateEmployee")
    public ResponseEntity<?> validateEmployee(@RequestBody UserIdDTO userIdDTO) {
        if (userIdDTO.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing userId to upgrade to validated employee");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.validateEmployee(userIdDTO));
        } catch (UsernameNotFoundException | CannotUpgradeToValidatedEmployee e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/category/addCategory")
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        if (categoryRequestDTO.getCategoryName() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing information to create a category");
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.addCategory(categoryRequestDTO));
        } catch (ExistingCategoryFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Category name already exists");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Category NOT created");
        }
    }

    @DeleteMapping("/category/deleteCategory")
    public ResponseEntity<?> deleteCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        if (categoryRequestDTO.getCategoryName() == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Missing information to delete a category");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(categoryService.deleteCategory(categoryRequestDTO));
        } catch (CategoryNotFoundException | CategoryHasAtLeastOneProductAssociated e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Category NOT deleted");
        }
    }

    @PostMapping("/dashboard/getDashboard")
    public ResponseEntity<?> getDashboardInformation(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getDashboardInformation(dashboardRequestDTO));
        } catch (InvalidDashboardRequestedDate e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/goals/addGoal")
    public ResponseEntity<?> addGoal(@RequestBody GoalDTO goalDTO) {
        return goalService.addGoal(goalDTO);
    }

    @DeleteMapping("/goals/deleteGoal")
    public ResponseEntity<?> deleteGoal(@RequestBody GoalDeleteDTO goalDeleteDTO) {
        return goalService.deleteGoal(goalDeleteDTO);
    }

    @PutMapping("/goals/modifyGoal")
    public ResponseEntity<?> modifyGoal(@RequestBody GoalModifyDTO goalModifyDTO) {
        return goalService.modifyGoal(goalModifyDTO);
    }

    @PostMapping("/goals/getGoals")
    public ResponseEntity<?> getGoals(@RequestBody GoalFilterRequestDTO goalFilterRequestDTO) {
        return goalService.getGoals(goalFilterRequestDTO);
    }

}
