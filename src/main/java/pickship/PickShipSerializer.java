package pickship;

/**
 * Created by tfd on 3/6/14.
 */
public class PickShipSerializer {

    public static String serialize(PickShip pickship) {
        String text = "PICK SHIP START\n";
        text += "ORDER NUMBER: " + pickship.getOrderNumber() + "\n";
        text += "TOTAL SHIP WEIGHT: " + pickship.getWeight() + "\n";
        for (Box box : pickship) {
            text += "BOXSTART:" + (box.getId() + 1) + "\n"; // human-readable (1-indexed) box number
            text += "SHIP WEIGHT: " + box.getWeight() + "\n";
            for (LineItem li : box) {
                text += "ITEM: " + li.getItem().getCode() + ", " + li.getQuantity() + "\n";
            }
            text += "BOX END\n";
        }
        text += "PICK SHIP END\n";
        return text;
    }
}
