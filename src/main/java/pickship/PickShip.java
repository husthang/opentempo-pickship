package pickship;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PickShip implements Iterable<Box> {

    private int orderNumber;
    private double weight;
    private List<Box> boxes;

    public PickShip(int orderNumber, Collection<Box> boxes) {
        this.orderNumber = orderNumber;
        this.boxes = new ArrayList<Box>(boxes);

        weight = 0.0;
        for (Box box : boxes) {
            weight += box.getWeight();
        }
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public Iterator<Box> iterator() {
        return boxes.iterator();
    }
}