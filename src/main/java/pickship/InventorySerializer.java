package pickship;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfd on 3/6/14.
 */
public class InventorySerializer {

    private enum InventoryParseState {START, INVENTORY, ITEM, CODE, NAME, WEIGHT, END}

    public static Inventory parse(String filePath) throws Exception {

        InventoryParseState state = InventoryParseState.START;
        Path file = Paths.get(filePath);
        List<Item> items = new ArrayList<>();
        String code = "";
        String name = "";
        double weight = -1.0;

        for (String line : Files.readAllLines(file, Charset.defaultCharset())) {
            line = line.trim();
            // Skip all blank lines
            if (line.equals("")) {
                continue;
            }
            switch(state) {
                case START:
                    if (line.equals("INVENTORY START")) {
                        state = InventoryParseState.INVENTORY;
                    } else {
                        throw new Exception("Missing INVENTORY START line.");
                    }
                    break;
                case INVENTORY:
                    if (line.equals("INVENTORY END")) {
                        state = InventoryParseState.END;
                    } else if (line.equals("ITEM START")) {
                        state = InventoryParseState.ITEM;
                    } else {
                        throw new Exception("Missing ITEM START or INVENTORY END line.");
                    }
                    break;
                case ITEM:
                    if (line.startsWith("CODE: ")) {
                        state = InventoryParseState.CODE;
                        code = line.split("\\s+", 2)[1];
                    } else {
                        throw new Exception("Missing CODE line.");
                    }
                    break;
                case CODE:
                    if (line.startsWith("NAME: ")) {
                        state = InventoryParseState.NAME;
                        name = line.split("\\s+", 2)[1];
                    } else {
                        throw new Exception("Missing NAME line.");
                    }
                    break;
                case NAME:
                    if (line.startsWith("WEIGHT: ")) {
                        state = InventoryParseState.WEIGHT;
                        weight = Double.parseDouble(line.split("\\s+", 2)[1]);
                    } else {
                        throw new Exception("Missing WEIGHT line.");
                    }
                    break;
                case WEIGHT:
                    if (line.equals("ITEM END")) {
                        state = InventoryParseState.INVENTORY;
                        items.add(new Item(code, name, weight));
                        code = name = "";
                        weight = -1.0;
                    } else {
                        throw new Exception("Missing ITEM END line.");
                    }
                    break;
                case END:
                    throw new Exception("Found a line after INVENTORY END line.");
                default:
                    throw new Exception("Unrecognized parsing state.");
            }
        }
        return new Inventory(items);
        // Dummy implementation
        // Item item = new Item("SHRT-RED", "Shirt (Red)", 2);
        // return new Inventory(Arrays.asList(item));
    }

    public static String serialize(Inventory inventory) {
        String text = "INVENTORY START\n";
        for (Item item : inventory) {
            text += "ITEM START\n";
            text += "CODE: " + item.getCode() + "\n";
            text += "NAME: " + item.getName() + "\n";
            text += "WEIGHT: " + item.getWeight() + "\n";
            text += "ITEM END\n";
        }
        text += "INVENTORY END\n";
        return text;
    }


}
