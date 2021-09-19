package service;

import model.Customer;

import java.util.*;

public enum CustomerService {

    /* provide a static reference using enum-singleton */
    INSTANCE;

    /* Solution 1: Singleton with static inner class */
//    private static class CustomerServiceHolder {
//        private static final CustomerService instance = new CustomerService();
//    }
//
//    private CustomerService() {}
//    public static CustomerService getInstance() {
//        return CustomerServiceHolder.instance;
//    }

    private final Map<String, Customer> map = new HashMap<>(); // map<email, customer>

    public void addCustomer(String email, String firstName, String lastName) {
        Customer newCustomer = new Customer(firstName, lastName, email);
        map.put(email, newCustomer);
    }

    public Customer getCustomer(String input) {
        String lowerCaseInput = input.toLowerCase();
        return map.getOrDefault(lowerCaseInput, null);
    }

    public Collection<Customer> getAllCustomers() {
        return map.values();
    }

    public Collection<String> getAllCustomersEmails() {
        Collection<String> emails = new ArrayList<>();
        map.values().forEach(Customer -> emails.add(Customer.getEmail()));
        return emails;
    }
}
