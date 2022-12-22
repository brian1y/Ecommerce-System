import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.StringTokenizer;
//Name: Brian Yang, Student ID: 501116230
// Simulation of a Simple E-Commerce System (like Amazon)

public class ECommerceUserInterface
{
	public static void main(String[] args)
	{
		// Create the system
		ECommerceSystem amazon = new ECommerceSystem();

		Scanner scanner = new Scanner(System.in);
		System.out.print(">");

		// Process keyboard actions
		while (scanner.hasNextLine())
		{
			String action = scanner.nextLine();
			try {
				if (action == null || action.equals("")) 
				{
					System.out.print("\n>");
					continue;
				}
				else if (action.equalsIgnoreCase("Q") || action.equalsIgnoreCase("QUIT"))
					return;

				else if (action.equalsIgnoreCase("PRODS"))	// List all products for sale
				{
					amazon.printAllProducts(); 
				}
				else if (action.equalsIgnoreCase("BOOKS"))	// List all books for sale
				{
					amazon.printAllBooks(); 
				}
				else if (action.equalsIgnoreCase("BOOKSBYAUTHOR"))	// ship an order to a customer
				{
					String author = "";

					System.out.print("Author: ");
					if (scanner.hasNextLine())
						author = scanner.nextLine();

					ArrayList<Book> books = amazon.booksByAuthor(author);
					Collections.sort(books);
					for (Book book: books)
						book.print();
				}
				else if (action.equalsIgnoreCase("CUSTS")) 	// List all registered customers
				{
					amazon.printCustomers();	
				}
				else if (action.equalsIgnoreCase("ORDERS")) // List all current product orders
				{
					amazon.printAllOrders();	
				}
				else if (action.equalsIgnoreCase("SHIPPED"))	// List all orders that have been shipped
				{
					amazon.printAllShippedOrders();	
				}
				else if (action.equalsIgnoreCase("NEWCUST"))	// Create a new registered customer
				{
					String name = "";
					String address = "";

					System.out.print("Name: ");
					if (scanner.hasNextLine())
						name = scanner.nextLine();

					System.out.print("\nAddress: ");
					if (scanner.hasNextLine())
						address = scanner.nextLine();

					amazon.createCustomer(name, address);
				}
				else if (action.equalsIgnoreCase("SHIP"))	// ship an order to a customer
				{
					String orderNumber = "";

					System.out.print("Order Number: ");
					if (scanner.hasNextLine())
						orderNumber = scanner.nextLine();

					ProductOrder order = amazon.shipOrder(orderNumber);
					if (order != null)
					{
						order.print();
					}
					else 
					{
						System.out.println(amazon.getErrorMessage());
					}
				}
				else if (action.equalsIgnoreCase("CUSTORDERS")) // List all the current orders and shipped orders for this customer
				{
					String customerId = "";

					System.out.print("Customer Id: ");
					if (scanner.hasNextLine())
						customerId = scanner.nextLine();

					// Prints all current orders and all shipped orders for this customer
					amazon.printOrderHistory(customerId);
				}
				else if (action.equalsIgnoreCase("ORDER")) // order a product for a certain customer
				{
					String productId = "";
					String customerId = "";

					System.out.print("Product Id: ");
					if (scanner.hasNextLine())
						productId = scanner.nextLine();

					System.out.print("\nCustomer Id: ");
					if (scanner.hasNextLine())
						customerId = scanner.nextLine();

					String orderNumber = amazon.orderProduct(productId, customerId, "");

					System.out.println("Order #" + orderNumber);
				}
				else if (action.equalsIgnoreCase("ORDERBOOK")) // order a book for a customer, provide a format (Paperback, Hardcover or EBook)
				{
					String productId = "";
					String customerId = "";
					String format = "";

					System.out.print("Product Id: ");
					if (scanner.hasNextLine())
						productId = scanner.nextLine();

					System.out.print("\nCustomer Id: ");
					if (scanner.hasNextLine())
						customerId = scanner.nextLine();

					System.out.print("\nFormat [Paperback Hardcover EBook]: ");
					if (scanner.hasNextLine())
						format = scanner.nextLine();

					String orderNumber = amazon.orderProduct(productId, customerId, format);

						
					System.out.println("Order #" + orderNumber);
				}
				else if (action.equalsIgnoreCase("ORDERSHOES")) // order a book for a customer, provide a format (Paperback, Hardcover or EBook)
				{
					String productId = "";
					String customerId = "";
					String sizeColor = "";

					System.out.print("Product Id: ");
					if (scanner.hasNextLine())
						productId = scanner.nextLine();

					System.out.print("\nCustomer Id: ");
					if (scanner.hasNextLine())
						customerId = scanner.nextLine();

					System.out.print("\nSize (6, 7, 8, 9, 10) and Color (Black or Brown): ");
					if (scanner.hasNextLine())
						sizeColor = scanner.nextLine();

					String orderNumber = amazon.orderProduct(productId, customerId, sizeColor);

					System.out.println("Order #" + orderNumber);
				}
				else if (action.equalsIgnoreCase("CANCEL")) // Cancel an existing order
				{
					String orderNumber = "";

					System.out.print("Order Number: ");
					if (scanner.hasNextLine())
						orderNumber = scanner.nextLine();

					amazon.cancelOrder(orderNumber);
				}
				else if (action.equalsIgnoreCase("SORTBYPRICE")) // sort products by price
				{
					amazon.printByPrice();
				}
				else if (action.equalsIgnoreCase("SORTBYNAME")) // sort products by name (alphabetic)
				{
					amazon.printByName();
				}
				else if (action.equalsIgnoreCase("SORTCUSTS")) // sort products by name (alphabetic)
				{
					amazon.sortCustomersByName();
				}
				else if (action.equalsIgnoreCase("ADDTOCART")) // Adds a product to customer cart
				{
					String productId = "";
					String customerId = "";
					String productOptions = "";

					System.out.print("Product Id: ");
					if (scanner.hasNextLine())
						productId = scanner.nextLine();

					System.out.print("\nCustomer Id: ");
					if (scanner.hasNextLine())
						customerId = scanner.nextLine();

					System.out.print("\n[Book Options: Paperback, Ebook, Hardcover]\n[Shoes Options: Size(6-10) + Colour(black,brown) (e.g. \"6 black\")]");
					System.out.print("\nProduct Option: ");
					if (scanner.hasNextLine())
						productOptions = scanner.nextLine();

					amazon.addToCart(productId, customerId, productOptions);
					
				}
				else if (action.equalsIgnoreCase("REMCARTITEM")) // Removes a product from customer cart
				{
					String productId = "";
					String customerId = "";

					System.out.print("Product Id: ");
					if (scanner.hasNextLine())
						productId = scanner.nextLine();

					System.out.print("\nCustomer Id: ");
					if (scanner.hasNextLine())
						customerId = scanner.nextLine();

					amazon.removeFromCart(productId, customerId);
				}
				else if (action.equalsIgnoreCase("PRINTCART")) // Prints products in customer cart
				{
					String customerId = "";

					System.out.print("\nCustomer Id: ");
					if (scanner.hasNextLine())
						customerId = scanner.nextLine();

					amazon.printCart(customerId);
				}
				else if (action.equalsIgnoreCase("ORDERITEMS")) // order items in customer cart
				{
					String customerId = "";

					System.out.print("\nCustomer Id: ");
					if (scanner.hasNextLine())
						customerId = scanner.nextLine();

					amazon.orderItems(customerId);
				}
				else if (action.equalsIgnoreCase("STATS")) // print order count stats for all products
				{
					amazon.printStats();
				}
				else if (action.equalsIgnoreCase("RATE")) // rate a product from 1 to 5
				{
					String productId = "";
					String rating = "";

					System.out.print("Product Id: ");
					if (scanner.hasNextLine())
						productId = scanner.nextLine();

					System.out.print("Rating (1 to 5): ");
					if (scanner.hasNextLine())
						rating = scanner.nextLine();

					amazon.addRating(productId, rating);

				}
				else if (action.equalsIgnoreCase("PRODRATING")) // prints average rating for a product
				{
					String productId = "";

					System.out.print("Product Id: ");
					if (scanner.hasNextLine())
						productId = scanner.nextLine();

					amazon.printRating(productId);
				}
				else if (action.equalsIgnoreCase("LISTRATINGS")) // list ratings for all products in a category above a threshold
				{
					String category = "";
					String rating = "";

					System.out.print("Product Category: ");
					if (scanner.hasNextLine())
						category = scanner.nextLine();

					System.out.print("Rating threshold (0 to 4): ");
					if (scanner.hasNextLine())
						rating = scanner.nextLine();

					amazon.printCategoryRatings(category,rating);
				}

			}
			catch(UnknownCustomerException e) {
				System.out.println(e.getMessage());
			}
			catch(UnknownProductException e) {
				System.out.println(e.getMessage());
			}
			catch(InvalidOptionException e) {
				System.out.println(e.getMessage());
			}
			catch(productOutOfStockException e) {
				System.out.println(e.getMessage());
			}
			catch(InvalidCustomerNameException e) {
				System.out.println(e.getMessage());
			}
			catch(InvalidCustomerAddressException e) {
				System.out.println(e.getMessage());
			}
			catch(InvalidOrderNumberException e) {
				System.out.println(e.getMessage());
			}
			catch(InvalidRatingException e) {
				System.out.println(e.getMessage());
			}
			catch(InvalidCategoryException e) {
				System.out.println(e.getMessage());
			}
			System.out.print("\n>");
		}
	}
}
