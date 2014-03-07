package pickship;

import pickship.packer.FirstFitDescendingPacker;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * From the command line, generate a pick-and-ship list for an inventory and order.
 *
 * Read an inventory and an order and output a pick-and-ship list detailing which items in the
 * order go into which boxes.  The application uses a bin packing algorithm to find an approximate solution
 * to the problem of minimizing the number of boxes used to ship an order.
 *
 * This console application implements an OpenTempo programming exercise.
 */
public class PickShipApp {

    private static final Double boxCapacity = 10.0;

    /**
     * Read command line args, process files, generate PickShip, and output it.
     *
     * @param args Contains the paths to the inventory file and order file.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	// Process command line arguments
    	String usage = "Usage: java pickship.PickShipApp <inventory file> <order file>";
    	if (args.length != 2) {
    		System.err.println(usage);
    		System.err.println("Exactly 2 arguments are required: an inventory text file and an order text file.");
        	System.exit(1);
    	}
    	String inventoryFile = args[0];
    	String orderFile = args[1];

    	// Parse inventory
        List<String> inventoryLines = Files.readAllLines(Paths.get(inventoryFile), Charset.defaultCharset());
    	Inventory inventory = InventorySerializer.parse(inventoryLines);
        // Confirm that parsing of Inventory looks good.
        // System.out.println(InventorySerializer.serialize(inventory));

        // Parse order
        List<String> orderLines = Files.readAllLines(Paths.get(orderFile), Charset.defaultCharset());
    	Order order = OrderSerializer.parse(orderLines, inventory);
        // Confirm that parsing of Order looks good.
        // System.out.println(OrderSerializer.serialize(order));

    	// Pick-and-ship
    	PickShip pickship = generatePickShip(order);

    	// Serialize and output Pick-and-ship
    	System.out.print(PickShipSerializer.serialize(pickship));

    }

    /**
     * Run an order through a bin packing algorithm, that attempts to minimize the number of boxes needed to pack the
     * order. Return this result as a PickShip.
     *
     * @param order
     * @return a PickShip describing how to pack order into boxes.
     * @throws Exception
     */
    public static PickShip generatePickShip(Order order) throws Exception {
        // Unroll Order -> line items -> items -> weights
        List<Item> items = new ArrayList<>();
        for (LineItem li : order) {
            for (int i = 0; i < li.getQuantity(); i++) {
                items.add(li.getItem());
            }
        }
        Double[] weights = new Double[items.size()];
        for (int i = 0; i < items.size(); i++) {
            weights[i] = items.get(i).getWeight();
        }

        // Pack weights
        Integer[] binIndices = new FirstFitDescendingPacker().pack(weights, boxCapacity);
        // Integer[] binIndices = new NextFitPacker().pack(weights, boxCapacity);
        // Confirm that the bin indices look reasonable.
        // System.err.println(Arrays.toString(binIndices));

        // Reroll Order from box indices -> weights in boxes -> items in boxes -> line items in boxes -> PickShip.
        // Iterate through each binIndex and item, assigning the item to a bin list, based on the binIndex.
        Map<Integer, List<Item>> bins = new HashMap<>();
        for (int i = 0; i < binIndices.length; i++) {
            int binIndex = binIndices[i];
            if (!bins.containsKey(binIndex)) {
                bins.put(binIndex, new ArrayList<Item>());
            }
            bins.get(binIndex).add(items.get(i));
        }
        List<Box> boxes = new ArrayList<>();
        List<Integer> boxIds = new ArrayList<>(bins.keySet());
        Collections.sort(boxIds);
        for (int boxId : boxIds) {
            List<Item> binItems = bins.get(boxId);
            // Count the number of occurrences of each item in the bin, to create LineItems
            Map<Item, Integer> counts = new HashMap<>();
            for (Item item : bins.get(boxId)) {
                if (!counts.containsKey(item)) {
                    counts.put(item, 0);
                }
                counts.put(item, counts.get(item) + 1);
            }
            List<LineItem> lineItems = new ArrayList<>();
            for (Item item : counts.keySet()) {
                lineItems.add(new LineItem(item, counts.get(item)));
            }
            boxes.add(new Box(boxId, lineItems));
        }
        PickShip pickShip = new PickShip(order.getNumber(), boxes);
        return pickShip;
    }
}