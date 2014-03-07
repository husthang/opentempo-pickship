package pickship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tfd on 3/5/14.
 */
public class Box implements Iterable<LineItem>{

    private int id;
    private double weight = 0.0;
    private List<LineItem> lineItems;

    /**
     * Create a Box containing the given LineItems.
     *
     * The box weight is automatically calculated from the given LineItems.
     *
     * @param id Corresponds to the Box Number in a PickShip document.
     * @param lineItems The Items and quantities this box contains.
     */
    public Box(int id, Collection<LineItem> lineItems) {
        this.id = id;
        this.lineItems = new ArrayList<LineItem>(lineItems);

        weight = 0.0;
        for (LineItem li : lineItems) {
            weight += li.getQuantity() * li.getItem().getWeight();
        }

    }

    public int getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public Iterator<LineItem> iterator() {
        return lineItems.iterator();
    }
}
