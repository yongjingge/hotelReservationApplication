package model;

public class CustomerTester {

    public static void main(String[] args) {
        Customer customer = new Customer("tom", "smith", "tom@gmail.com");
        System.out.println(customer);

        Customer anotherCustomer = new Customer("amy", "brown", "@com");
        System.out.println(anotherCustomer);
    }
}
