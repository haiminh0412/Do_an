package com.example.car_management.service.implement;

import com.example.car_management.dto.CustomerDTO;
import com.example.car_management.entity.Customer;
import com.example.car_management.exception.AppException;
import com.example.car_management.exception.ErrorCode;
import com.example.car_management.repository.ICustomerRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerService {
    @Autowired
    private final ICustomerRepository customerRepository;

    @Autowired
    private final ModelMapper modelMapper;

    public CustomerDTO insert(final CustomerDTO customerRequest) {
        // convert DTO to entity
        Customer customer = modelMapper.map(customerRequest, Customer.class);

//        if(customerRepository.existsCustomerByNameAndPhoneAndEmail(
//                customer.getName(), customer.getPhone(), customer.getEmail()))
//            return modelMapper.map(customerRepository.findByEmail(customer.getEmail()), CustomerDTO.class);

        Customer newCustomer =  customerRepository.save(customer);

        // convert entity to DTO
        CustomerDTO customerResponse = modelMapper.map(newCustomer, CustomerDTO.class);

        return customerResponse;
    }

    public CustomerDTO findById(final Integer id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ID));
        return modelMapper.map(customer, CustomerDTO.class);
    }

//    public CustomerDTO findByCustomer(final CustomerDTO customerRequest) {
//        Customer customer = customerRepository.findDistinctNameAndPhoneAndEmail(customerRequest.getName(), customerRequest.getPhone(), customerRequest.getEmail()).getLast();
//       return modelMapper.map(customer, CustomerDTO.class);
//    }

//    public CustomerResponse insert(CustomerRequest customerRequest) {
//        String name = StringUtils.normalizeString(customerRequest.getName());
//        customerRequest.setName(name);
//
////        if(customerRepository.existsCustomerByNameAndPhoneAndEmail(
////                customerRequest.getName(),
////                customerRequest.getPhone(),
////                customerRequest.getEmail())) {
////            throw new AppException(ErrorCode.EXIST);
////        }
//
//        // convert DTO to entity
//        Customer customer = modelMapper.map(customerRequest, Customer.class);
//
//        Customer newCustomer =  customerRepository.save(customer);
//
//        // convert entity to DTO
//        CustomerResponse customerResponse = modelMapper.map(newCustomer, CustomerResponse.class);
//        customerResponse.setCustomerRequest(customerRequest);
//
//        return customerResponse;
//    }

    public List<CustomerDTO> findAll() {
        List<CustomerDTO> customers = customerRepository.findAll().stream().map(
                customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
        return customers;
    }
}
