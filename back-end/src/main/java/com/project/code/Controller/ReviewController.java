package com.project.code.Controller;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/{storeId}/{productId}")
public Map<String, Object> getReviews(@PathVariable Long storeId, @PathVariable Long productId) {
    Map<String, Object> response = new HashMap<>();
    List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);
    List<Map<String, Object>> reviewData = reviews.stream().map(review -> {
        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("comment", review.getComment());
        reviewMap.put("rating", review.getRating());
        
        // Use Optional to get Customer
        Customer customer = customerRepository.findById(review.getCustomerId())
                                    .orElse(null); // Handle not found gracefully
        
        reviewMap.put("customerName", customer != null ? customer.getName() : "Unknown");
        return reviewMap;
    }).collect(Collectors.toList());
    response.put("reviews", reviewData);
    return response;
}

}