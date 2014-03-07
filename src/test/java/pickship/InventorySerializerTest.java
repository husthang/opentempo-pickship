package pickship;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfd on 3/6/14.
 */
public class InventorySerializerTest {

    private static List<String> testLines;
    private static String testString;
    static {
        testLines = new ArrayList<>();
        testLines.add("INVENTORY START\n");
        testLines.add("ITEM START\n");
        testLines.add("CODE: SHRT-RED\n");
        testLines.add("NAME: Shirt (Red)\n");
        testLines.add("WEIGHT: 2.0\n");
        testLines.add("ITEM END\n");
        testLines.add("ITEM START\n");
        testLines.add("CODE: CELL_PHONE1\n");
        testLines.add("NAME: Cell Phone 1\n");
        testLines.add("WEIGHT: 4.3\n");
        testLines.add("ITEM END\n");
        testLines.add("INVENTORY END\n");

        testString = "";
        for (String line : testLines) {
            testString += line;
        }
    }

    private static Inventory testInventory;
    static {
        List<Item> items = new ArrayList<>();
        items.add(new Item("SHRT-RED", "Shirt (Red)", 2.0));
        items.add(new Item("CELL_PHONE1", "Cell Phone 1", 4.3));
        testInventory = new Inventory(items);
    }

    /**
     * Test that parsing an inventory string into an Inventory and then serializing the Inventory results in
     * the original inventory string.  Whitespace and number printing issues mean that this is not generally true, only
     * for cases lacking extraneous whitespaces and with weights that parse and print as the same double.
     *
     * @throws Exception
     */
    @Test
    public void testParseAndSerialize() throws Exception {
        org.junit.Assert.assertEquals("Failure.  Serialized-parsed string not equal to original string.",
                testString, InventorySerializer.serialize(InventorySerializer.parse(testLines)));
    }

    @Test
    public void testSerialize() throws Exception {

    }
}
