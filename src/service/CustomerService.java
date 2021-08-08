package service;

import model.Customer;

import java.util.*;

public class CustomerService {

    /* Solution 1: Singleton with nested static class */
    private static class CustomerServiceHolder {
        private static final CustomerService instance = new CustomerService();
    }

    private CustomerService() {}
    public static CustomerService getInstance() {
        return CustomerServiceHolder.instance;
    }

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

    /* Solution 2: Singleton with Enum */
    /*
    INSTANCE;
    private Map<String, Customer> map;

    public void addCustomer(String email, String firstName, String lastName) {
        Customer newCustomer = new Customer(firstName, lastName, email);
        map.put(email, newCustomer);
    }

    public Customer getCustomer(String input) {
        String lowercaseInput = input.toLowerCase();
        return map.containsKey(input) ? map.get(lowercaseInput) : null;
    }

    public Collection<Customer> getAllCustomers() {
        return map.values();
    }
    */
}
