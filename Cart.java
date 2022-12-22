import java.util.ArrayList;
//Name: Brian Yang, Student ID: 501116230
/**
 * Class Cart defines a customer's shopping cart and provides functionality
 * such as adding and removing products and printing the contents of cart
 */
public class Cart 
{
    private ArrayList<CartItem> items;

    public Cart() 
    {
        items = new ArrayList<CartItem>();
    }
    // adds product as a cartitem into items arraylist
    public void addToCart(Product prod, String options) 
    {
        items.add(new CartItem(prod, options));
    }
    // removes first occurence of a product from items arraylist
    public void removeFromCart(Product prod) 
    {
        items.remove(new CartItem(prod));
    }
    public void print() 
    {   
        if (items.isEmpty()) 
            System.out.println("\nThere are no items in the cart");
        else {
            for (CartItem i : items) {
                Product prod = i.getProduct();
                prod.print();
            }
        }
    }

    public CartItem getItem(int index) 
    {
        return items.get(index);
    }
    // returns number of items in the cart
    public int itemCount() 
    {
        return items.size();
    }
    // removes all items from cart
    public void emptyCart() 
    {
        items.clear();
    }
}
