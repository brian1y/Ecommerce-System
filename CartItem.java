//Name: Brian Yang, Student ID: 501116230
/**
 * Class CartItem defines the item to be put into a customer's cart
 * keeping track of the product and product option
 */
public class CartItem 
{
    private Product product;
    private String productOptions;

    public CartItem(Product prod) 
    {
        product = prod;
        productOptions = "";
    }
    public CartItem(Product prod, String options) 
    {
        product = prod;
        productOptions = options;
    }
    public Product getProduct() {
        return product;
    }
    public String getProductOption() {
        return productOptions;
    }
    public boolean equals(Object other)
	{
		CartItem otherI = (CartItem) other;
		return this.product.equals(otherI.product);
	}
}