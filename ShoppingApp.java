import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// --------------------- Product Classes ---------------------
class Product {
    private String name;
    private int price;
    private int quantity;

    public Product(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getInfo() {
        return name + " - " + price + " EGP (In stock: " + quantity + ")";
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

class ExpirableProduct extends Product {
    private LocalDate expirationDate;

    public ExpirableProduct(String name, int price, int quantity, LocalDate expirationDate) {
        super(name, price, quantity);
        this.expirationDate = expirationDate;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }
}

class ShippableProduct extends Product {
    private int weight;  // weight per item in grams

    public ShippableProduct(String name, int price, int quantity, int weight) {
        super(name, price, quantity);
        this.weight = weight;
    }

    public String getName() {
        return super.getName();
    }

    public int getWeight() {
        return weight;
    }
}

class ExpirableShippableProduct extends Product {
    private LocalDate expirationDate;
    private int weight;

    public ExpirableShippableProduct(String name, int price, int quantity, LocalDate expirationDate, int weight) {
        super(name, price, quantity);
        this.expirationDate = expirationDate;
        this.weight = weight;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    public String getName() {
        return super.getName();
    }

    public int getWeight() {
        return weight;
    }
}

// --------------------- Customer ---------------------
class Customer {
    private String name;
    private int balance;

    public Customer(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}

// --------------------- Cart ---------------------
class Cart {
    private Map<Product, Integer> items = new HashMap<>();

    public void add(Product product, int quantity) throws Exception {
        if (quantity > product.getQuantity()) {
            throw new Exception("Not enough stock for " + product.getName() + "!");
        }
        if (items.containsKey(product)) {
            items.put(product, items.get(product) + quantity);
        } else {
            items.put(product, quantity);
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Map<Product, Integer> getItems() {
        return items;
    }
}

// --------------------- Shipping ---------------------
class ShippingService {
    static class Item {
        String name;
        int weight;
        int qty;
        Item(String name, int weight, int qty) {
            this.name = name;
            this.weight = weight;
            this.qty = qty;
        }
    }

    public void ship(List<Item> items) {
        System.out.println("** Shipment notice **");
        int totalWeight = 0;
        for (Item item : items) {
            System.out.println(item.qty + "x " + item.name + " " + item.weight + "g");
            totalWeight += item.weight;
        }
        System.out.printf("Total package weight %.1fkg\n", totalWeight / 1000.0);
    }
}

// --------------------- Checkout ---------------------
public class ShoppingApp {
    public static void checkout(Customer customer, Cart cart) throws Exception {
        if (cart.isEmpty()) {
            throw new Exception("Cart is empty!");
        }

        int subtotal = 0;
        List<ShippingService.Item> shippableItems = new ArrayList<>();

        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product product = entry.getKey();
            int qty = entry.getValue();

            if (product instanceof ExpirableProduct) {
                ExpirableProduct expirable = (ExpirableProduct) product;
                if (expirable.isExpired()) {
                    throw new Exception(product.getName() + " is expired!");
                }
            }
            if (product instanceof ExpirableShippableProduct) {
                ExpirableShippableProduct expirable = (ExpirableShippableProduct) product;
                if (expirable.isExpired()) {
                    throw new Exception(product.getName() + " is expired!");
                }
            }

            if (qty > product.getQuantity()) {
                throw new Exception("Not enough " + product.getName() + " in stock!");
            }

            subtotal += product.getPrice() * qty;

            if (product instanceof ShippableProduct) {
                ShippableProduct shippable = (ShippableProduct) product;
                int itemWeight = shippable.getWeight() * qty;
                shippableItems.add(new ShippingService.Item(product.getName(), itemWeight, qty));
            }
            if (product instanceof ExpirableShippableProduct) {
                ExpirableShippableProduct shippable = (ExpirableShippableProduct) product;
                int itemWeight = shippable.getWeight() * qty;
                shippableItems.add(new ShippingService.Item(product.getName(), itemWeight, qty));
            }
        }

        int shippingFee = 0;
        if (!shippableItems.isEmpty()) {
            int totalWeight = 0;
            for (ShippingService.Item item : shippableItems) {
                totalWeight += item.weight;
            }
            if (totalWeight <= 1000) {
                shippingFee = 20;
            } else {
                double extraKg = (totalWeight - 1000) / 1000.0;
                shippingFee = 20 + (10 * (int)Math.round(extraKg));
            }
        }

        int total = subtotal + shippingFee;

        if (total > customer.getBalance()) {
            throw new Exception("Insufficient balance!");
        }

        if (!shippableItems.isEmpty()) {
            ShippingService shippingService = new ShippingService();
            shippingService.ship(shippableItems);
        }

        customer.setBalance(customer.getBalance() - total);
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product product = entry.getKey();
            int qty = entry.getValue();
            product.setQuantity(product.getQuantity() - qty);
        }

        System.out.println("\n** Checkout receipt **");
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product product = entry.getKey();
            int qty = entry.getValue();
            System.out.printf("%dx %-15s%d\n", qty, product.getName(), product.getPrice() * qty);
        }
        System.out.println("----------------------");
        System.out.println("Subtotal         " + subtotal);
        System.out.println("Shipping         " + shippingFee);
        System.out.println("Amount           " + total);
        System.out.println("Balance          " + customer.getBalance());
    }

    // --------------------- Example ---------------------
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        ExpirableShippableProduct cheese = new ExpirableShippableProduct("Cheese", 100, 6, today.plusDays(3), 200);
        ExpirableShippableProduct biscuits = new ExpirableShippableProduct("Biscuits", 150, 3, today.plusDays(2), 700);
        Product scratch = new Product("Scratch Card", 50, 10);
        ShippableProduct tv = new ShippableProduct("TV", 2000, 2, 5000);

        Customer customer = new Customer("Ahmed", 1000);
        Cart cart = new Cart();

        try {
            cart.add(cheese, 5);
            cart.add(biscuits, 2);
            cart.add(scratch, 1);
            checkout(customer, cart);
        } catch (Exception e) {
            System.out.println("Checkout failed: " + e.getMessage());
        }
    }
}
