package com.example.car_management.repository;

import com.example.car_management.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer,Integer> {
    boolean existsCustomerByNameAndPhoneAndEmail(String customerName,String phone,String email);
 //   List<Customer> findDistinctNameAndPhoneAndEmail(String customerName, String phone, String email);
    boolean existsCustomerByEmail(String email);
    Customer findByEmail(String email);
}
