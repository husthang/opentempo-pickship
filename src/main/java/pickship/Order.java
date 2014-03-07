package pickship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Order implements Iterable<LineItem>{
	private int number;
	private String customerCode;
	private List<LineItem> lineItems;

	public Order(int number, String customerCode, Collection<LineItem> lineItems) {
		this.number = number;
		this.customerCode = customerCode;
		this.lineItems = new ArrayList<LineItem>(lineItems);
	}

    public int getNumber() { return number; }

    public String getCustomerCode() {
        return customerCode;
    }

    public Iterator<LineItem> iterator() {
        return lineItems.iterator();
    }

}