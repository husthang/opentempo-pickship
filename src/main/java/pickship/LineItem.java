package pickship;

public class LineItem {

    private Item item;
    private int quantity;

    public LineItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        if (quantity <= 0) {
            throw new IllegalArgumentException("Line item quantity must be > 0.");
        }
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }
}