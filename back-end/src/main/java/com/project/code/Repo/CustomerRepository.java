package com.project.code.Repo;

import com.project.code.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email); // Change to Optional<Customer>
    Optional<Customer> findById(Long id); // Change to Optional<Customer>
}
