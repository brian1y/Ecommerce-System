import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
//Name: Brian Yang, Student ID: 501116230
/*
 * Models a simple ECommerce system. Keeps track of products for sale, registered customers, product orders and
 * orders that have been shipped to a customer
 */
public class ECommerceSystem
{
	private HashMap<String,Product> products = new HashMap<String,Product>();
	private ArrayList<Customer> customers = new ArrayList<Customer>();	

	private ArrayList<ProductOrder> orders = new ArrayList<ProductOrder>();
	private ArrayList<ProductOrder> shippedOrders = new ArrayList<ProductOrder>();

	private HashMap<Product,Integer> productOrderStats = new HashMap<Product,Integer>();

	// These variables are used to generate order numbers, customer id's, product id's 
	private int orderNumber = 500;
	private int customerId = 900;
	private int productId = 700;

	// General variable used to store an error message when something is invalid (e.g. customer id does not exist)  
	private String errMsg = null;

	// Random number generator
	private Random random = new Random();

	public ECommerceSystem()
	{
		// Create some customers
		customers.add(new Customer(generateCustomerId(),"Inigo Montoya", "1 SwordMaker Lane, Florin"));
		customers.add(new Customer(generateCustomerId(),"Prince Humperdinck", "The Castle, Florin"));
		customers.add(new Customer(generateCustomerId(),"Andy Dufresne", "Shawshank Prison, Maine"));
		customers.add(new Customer(generateCustomerId(),"Ferris Bueller", "4160 Country Club Drive, Long Beach"));
		try {
			// create products map from txt file
			ArrayList<Product> prods = productReader("products.txt");
			for (Product p : prods) {
				products.put(p.getId(), p);
				productOrderStats.put(p,0);
			}
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	private ArrayList<Product> productReader(String filename) throws IOException {
		Scanner input = new Scanner(new File(filename));
		String line;
		ArrayList<Product> list = new ArrayList<Product>();
		Product.Category prodCategory = null;
		String productName;
		double productPrice;
		int stock = 0;
		int[] bookStocks = new int[2];
		String[] bookInfo = new String[3];

		while (input.hasNextLine()) {
			//1st line
			line = input.nextLine();
			prodCategory = Product.Category.valueOf(line.toUpperCase());
			productName = input.nextLine(); //2nd line
			productPrice = Double.parseDouble(input.nextLine()); //3rd line
			//4th line
			line = input.nextLine();
			if (prodCategory == Product.Category.BOOKS) {
				String[] temp = line.split(" ");;
				bookStocks[0] = Integer.parseInt(temp[0]);
				bookStocks[1] = Integer.parseInt(temp[1]);
			}
			else {
				stock = Integer.parseInt(line);
			}
			//5th line
			line = input.nextLine();
			if (prodCategory == Product.Category.BOOKS) {
				bookInfo = line.split(":");
				list.add(new Book(productName, generateProductId(), productPrice, bookStocks[0], bookStocks[1], bookInfo[0],  bookInfo[1], Integer.parseInt(bookInfo[2])));
			}
			else {
				list.add(new Product(productName, generateProductId(), productPrice, stock, prodCategory));
			}
		}
		return list;
	}

	private String generateOrderNumber()
	{
		return "" + orderNumber++;
	}

	private String generateCustomerId()
	{
		return "" + customerId++;
	}

	private String generateProductId()
	{
		return "" + productId++;
	}

	public String getErrorMessage()
	{
		return errMsg;
	}

	public void printAllProducts()
	{
		for (Product p : products.values())
			p.print();
	}

	public void printAllBooks()
	{
		for (Product p : products.values())
		{
			if (p.getCategory() == Product.Category.BOOKS)
				p.print();
		}
	}

	public ArrayList<Book> booksByAuthor(String author)
	{
		ArrayList<Book> books = new ArrayList<Book>();
		for (Product p : products.values())
		{
			if (p.getCategory() == Product.Category.BOOKS)
			{
				Book book = (Book) p;
				if (book.getAuthor().equals(author))
					books.add(book);
			}
		}
		return books;
	}

	public void printAllOrders()
	{
		for (ProductOrder o : orders)
			o.print();
	}

	public void printAllShippedOrders()
	{
		for (ProductOrder o : shippedOrders)
			o.print();
	}

	public void printCustomers()
	{
		for (Customer c : customers)
			c.print();
	}
	/*
	 * Given a customer id, print all the current orders and shipped orders for them (if any)
	 */
	public void printOrderHistory(String customerId)
	{
		// Make sure customer exists
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			errMsg = "Customer " + customerId + " Not Found";
			throw new UnknownCustomerException(errMsg);
		}	
		System.out.println("Current Orders of Customer " + customerId);
		for (ProductOrder order: orders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
		System.out.println("\nShipped Orders of Customer " + customerId);
		for (ProductOrder order: shippedOrders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
	}

	public String orderProduct(String productId, String customerId, String productOptions)
	{
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			errMsg = "Customer " + customerId + " Not Found";
			throw new UnknownCustomerException(errMsg);
		}
		Customer customer = customers.get(index);

		// Get product 
		if (!(products.containsKey(productId)))
		{
			errMsg = "Product " + productId + " Not Found";
			throw new UnknownProductException(errMsg);
		}
		Product product = products.get(productId);

		// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
		if (!product.validOptions(productOptions))
		{
			errMsg = "Product " + product.getName() + " ProductId " + productId + " Invalid Options: " + productOptions;
			throw new InvalidOptionException(errMsg);
		}
		// Is it in stock?
		if (product.getStockCount(productOptions) <= 0)
		{
			errMsg = "Product " + product.getName() + " ProductId " + productId + " Out of Stock";
			throw new productOutOfStockException(errMsg);
		}
		// Create a ProductOrder
		ProductOrder order = new ProductOrder(generateOrderNumber(), product, customer, productOptions);
		product.reduceStockCount(productOptions);

		// increase order count for product
		productOrderStats.replace(product,productOrderStats.get(product)+1);
		// Add to orders and return
		orders.add(order);
		return order.getOrderNumber();
	}

	/*
	 * Create a new Customer object and add it to the list of customers
	 */

	public void createCustomer(String name, String address)
	{
		// Check to ensure name is valid
		if (name == null || name.equals(""))
		{
			errMsg = "Invalid Customer Name " + name;
			throw new InvalidCustomerNameException(errMsg);
		}
		// Check to ensure address is valid
		if (address == null || address.equals(""))
		{
			errMsg = "Invalid Customer Address " + address;
			throw new InvalidCustomerAddressException(errMsg);
		}
		Customer customer = new Customer(generateCustomerId(), name, address);
		customers.add(customer);
	}

	public ProductOrder shipOrder(String orderNumber)
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			errMsg = "Order " + orderNumber + " Not Found";
			throw new InvalidOrderNumberException(errMsg);
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
		shippedOrders.add(order);
		return order;
	}

	/*
	 * Cancel a specific order based on order number
	 */
	public void cancelOrder(String orderNumber)
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			errMsg = "Order " + orderNumber + " Not Found";
			throw new InvalidOrderNumberException(errMsg);
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
	}
	// Sort products by increasing price
	public void printByPrice()
	{
		ArrayList<Product> prods = new ArrayList<Product>(products.values());
		Collections.sort(prods, new PriceComparator());
		for (Product p : prods)
			p.print();
	}

	private class PriceComparator implements Comparator<Product>
	{
		public int compare(Product a, Product b)
		{
			if (a.getPrice() > b.getPrice()) return 1;
			if (a.getPrice() < b.getPrice()) return -1;	
			return 0;
		}
	}

	// Sort products alphabetically by product name
	public void printByName()
	{
		ArrayList<Product> prods = new ArrayList<Product>(products.values());
		Collections.sort(prods, new NameComparator());
		for (Product p : prods)
			p.print();
	}

	private class NameComparator implements Comparator<Product>
	{
		public int compare(Product a, Product b)
		{
			return a.getName().compareTo(b.getName());
		}
	}

	// Sort products alphabetically by product name
	public void sortCustomersByName()
	{
		Collections.sort(customers);
	}
	
	//adds a product to customer's cart
	public void addToCart(String productId, String customerId, String options) {
		// checks if customer exists
		int custIndex = customers.indexOf(new Customer(customerId));
		if (custIndex == -1) {
			errMsg = "Customer " + customerId + " Not Found";
			throw new UnknownCustomerException(errMsg);
		}
		// checks if product exists
		if (!(products.containsKey(productId))) {
			errMsg = "Product " + productId + " Not Found";
			throw new UnknownProductException(errMsg);
		}
		
		Customer customer = customers.get(custIndex);
		Product product = products.get(productId);
		// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
		if (!product.validOptions(options))
		{
			errMsg = "Product " + product.getName() + " ProductId " + productId + " Invalid Options: " + options;
			throw new InvalidOptionException(errMsg);
		}
		// checks if product is out of stock
		if (product.getStockCount(options) <= 0)
		{
			errMsg = "Product " + product.getName() + " ProductId " + productId + " Out of Stock";
			throw new productOutOfStockException(errMsg);
		}
		
		customer.addToCart(product, options);
		product.reduceStockCount(options);
		
	}

	//removes a product from customer's cart
	public void removeFromCart(String productId, String customerId) {
		// checks if customer exists
		int custIndex = customers.indexOf(new Customer(customerId));
		if (custIndex == -1) {
			errMsg = "Customer " + customerId + " Not Found";
			throw new UnknownCustomerException(errMsg);
		}
		// checks if product exists
		if (!(products.containsKey(productId))) {
			errMsg = "Product " + productId + " Not Found";
			throw new UnknownProductException(errMsg);
		}
		// decrease order count for product
		Product product = products.get(productId);
		productOrderStats.replace(product,productOrderStats.get(product)-1);

		Customer customer = customers.get(custIndex);
		customer.removeFromCart(products.get(productId));
	}

	//prints all products in customer's cart
	public void printCart(String customerId) {
		// checks if customer exists
		int custIndex = customers.indexOf(new Customer(customerId));
		if (custIndex == -1) {
			errMsg = "Customer " + customerId + " Not Found";
			throw new UnknownCustomerException(errMsg);
		}
		Customer customer = customers.get(custIndex);
		customer.printCart();
	}
	// order all items in customer's cart
	public void orderItems(String customerId) {
		// checks if customer exists
		int custIndex = customers.indexOf(new Customer(customerId));
		if (custIndex == -1) {
			errMsg = "Customer " + customerId + " Not Found";
			throw new UnknownCustomerException(errMsg);
		}
		
		Customer customer = customers.get(custIndex);
		Cart custCart = customer.getCart();
		
		for (int i = 0; i < custCart.itemCount(); i++) {
			CartItem item = custCart.getItem(i);
			Product product = item.getProduct();
			String options = item.getProductOption();
			// Create a ProductOrder
			ProductOrder order = new ProductOrder(generateOrderNumber(), product, customer, options);
			// increase order count for product
			productOrderStats.replace(product,productOrderStats.get(product)+1);
			// Add to orders
			orders.add(order);
		}
		custCart.emptyCart();
	}
	// print order count stats for all products in descending order
	public void printStats() {
		ArrayList<Product> productList = new ArrayList<Product>(productOrderStats.keySet());
		ArrayList<Integer> orderCountList = new ArrayList<Integer>();
		Product sortedProd;
		// stores corresponding order count to orderCountList 
		for (Product key : productList) {
			orderCountList.add(productOrderStats.get(key));
		}
		int indexOfLargest;
		//sorts productid keys based on descending order count and stores it in sortKeys arraylist
		while (!(orderCountList.isEmpty())) {
			indexOfLargest = orderCountList.size()-1;
			for (int i = orderCountList.size()-2; i >= 0; i--) {
				if (orderCountList.get(i) > orderCountList.get(indexOfLargest))
					indexOfLargest = i;
			}
			sortedProd = productList.get(indexOfLargest);
			// prints stats for product
			System.out.printf("\nName: %-20s Id: %-5s Order Count: %d",sortedProd.getName(),sortedProd.getId(),productOrderStats.get(sortedProd));
			
			productList.remove(indexOfLargest);
			orderCountList.remove(indexOfLargest);
		}
	}

	// add rating to a product
	public void addRating(String productId, String rate) {
		// checks if product exists
		if (!(products.containsKey(productId))) {
			errMsg = "Product " + productId + " Not Found";
			throw new UnknownProductException(errMsg);
		}

		Product prod = products.get(productId);
		double rating;
		try {
			rating = Double.parseDouble(rate);
		}
		// check for invalid ratings
		catch (NumberFormatException e) {
			errMsg = "Invalid Rating: " + rate;
			throw new InvalidRatingException(errMsg);
		}
		if (rating < 1 || rating > 5) {
			errMsg = "Invalid Rating: " + rate;
			throw new InvalidRatingException(errMsg);
		}

		prod.addRating(rating);

	}
	// prints rating of a product
	public void printRating (String productId) {
		// checks if product exists
		if (!(products.containsKey(productId))) {
			errMsg = "Product " + productId + " Not Found";
			throw new UnknownProductException(errMsg);
		}

		Product prod = products.get(productId);
		if (prod.getRating() == null) 
			System.out.println("\nThere are no ratings for this product");
		else
			System.out.printf("\nName: %-20s Id: %-5s Rating: %.2f",prod.getName(),prod.getId(),prod.getRating());
	}
	// prints ratings from a product category above a specified threshold
	public void printCategoryRatings(String category, String rate) {
		Product.Category prodCategory;
		int rating;
		// check for invalid category
		try {
			prodCategory = Product.Category.valueOf(category.toUpperCase());
		}
		catch (IllegalArgumentException e) {
			throw new InvalidCategoryException("Invalid Category: " + category);
		}
		try {
			rating = Integer.parseInt(rate);
		}
		// check for invalid ratings
		catch (NumberFormatException e) {
			throw new InvalidRatingException("Invalid Rating: " + rate);
		}
		if (rating < 0 || rating > 4) {
			errMsg = "Invalid Rating: " + rate;
			throw new InvalidRatingException(errMsg);
		}

		for (Product prod : products.values()) {
			if (prod.getRating() != null && prod.getCategory() == prodCategory && prod.getRating() > rating)
				System.out.printf("\nName: %-20s Id: %-5s Rating: %.2f",prod.getName(),prod.getId(),prod.getRating());
		}
	}
}
//exception classes
class UnknownCustomerException extends RuntimeException {
	public UnknownCustomerException(String errorMessage) {
		super(errorMessage);
	}
}

class UnknownProductException extends RuntimeException {
	public UnknownProductException(String errorMessage) {
		super(errorMessage);
	}
}

class InvalidOptionException extends RuntimeException {
	public InvalidOptionException(String errorMessage) {
		super(errorMessage);
	}	
}

class productOutOfStockException extends RuntimeException {
	public productOutOfStockException(String errorMessage) {
		super(errorMessage);
	}	
}
class InvalidCustomerNameException extends RuntimeException {
	public InvalidCustomerNameException(String errorMessage) {
		super(errorMessage);
	}	
}

class InvalidCustomerAddressException extends RuntimeException {
	public InvalidCustomerAddressException(String errorMessage) {
		super(errorMessage);
	}	
}

class InvalidOrderNumberException extends RuntimeException {
	public InvalidOrderNumberException(String errorMessage) {
		super(errorMessage);
	}	
}

class InvalidRatingException extends RuntimeException {
	public InvalidRatingException(String errorMessage) {
		super(errorMessage);
	}
}

class InvalidCategoryException extends RuntimeException {
	public InvalidCategoryException(String errorMessage) {
		super(errorMessage);
	}
}



