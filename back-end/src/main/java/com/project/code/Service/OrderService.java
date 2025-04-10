package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        // 1. Retrieve or Create Customer
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        if (customer == null) {
            customer = new Customer(placeOrderRequest.getCustomerName(), placeOrderRequest.getCustomerEmail(), placeOrderRequest.getCustomerPhone());
            customerRepository.save(customer);
        }

        // 2. Retrieve Store
        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // 3. Create OrderDetails
        OrderDetails orderDetails = new OrderDetails(customer, store, placeOrderRequest.getTotalPrice(), LocalDateTime.now());
        orderDetailsRepository.save(orderDetails);

        // 4. Create and Save OrderItems
        List<PurchaseProductDTO> products = placeOrderRequest.getPurchaseProduct();
        for (PurchaseProductDTO productDTO : products) {
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(productDTO.getProductId(), store.getId());
            if (inventory != null && inventory.getStockLevel() >= productDTO.getQuantity()) {
                inventory.setStockLevel(inventory.getStockLevel() - productDTO.getQuantity());
                inventoryRepository.save(inventory);

                Product product = productRepository.findById(productDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                OrderItem orderItem = new OrderItem(orderDetails, product, productDTO.getQuantity(), product.getPrice());
                orderItemRepository.save(orderItem);
            } else {
                throw new RuntimeException("Insufficient stock for product ID: " + productDTO.getProductId());
            }
        }
    }
}