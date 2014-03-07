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
public class OrderSerializer {
    private enum OrderParseState {START, ORDER, NUMBER, CODE, ITEM, END}

    public static Order parse(List<String> lines, Inventory inventory) throws Exception {
        OrderParseState state = OrderParseState.START;
        int number = -1;
        String customerCode = "";
        List<LineItem> lineItems = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            // Skip all blank lines
            if (line.equals("")) {
                continue;
            }
            switch(state) {
                case START:
                    if (line.equals("ORDER START")) {
                        state = OrderParseState.ORDER;
                    } else {
                        throw new Exception("Missing ORDER START line.");
                    }
                    break;
                case ORDER:
                    if (line.startsWith("ORDER NUMBER: ")) {
                        state = OrderParseState.CODE;
                        number = Integer.parseInt(line.split(":\\s+", 2)[1]);
                    } else {
                        throw new Exception("Missing ORDER NUMBER line.");
                    }
                    break;
                case CODE:
                    if (line.startsWith("CUSTOMER CODE: ")) {
                        state = OrderParseState.ITEM;
                        customerCode = line.split(":\\s+", 2)[1];
                    } else {
                        throw new Exception("Missing CUSTOMER CODE line.");
                    }
                    break;
                case ITEM:
                    if (line.startsWith("ITEM: ")) {
                        state = OrderParseState.ITEM;
                        String[] codeAndQty = line.split("\\s+", 2)[1].split(",\\s+", 2);
                        String code = codeAndQty[0];
                        int qty = Integer.parseInt(codeAndQty[1]);
                        Item item = inventory.getItem(code);
                        lineItems.add(new LineItem(item, qty));
                    } else if (line.equals("ORDER END")) {
                        state = OrderParseState.END;
                    } else {
                        throw new Exception("Expected ITEM or ORDER END line.");
                    }
                    break;
                case END:
                    throw new Exception("Found a line after ORDER END line.");
                default:
                    throw new Exception("Unrecognized parsing state.");
            }
        }
        return new Order(number, customerCode, lineItems);

        // Dummy implementation
        // Item item = new Item("SHRT-RED", "Shirt (Red)", 2);
        // LineItem li = new LineItem(item, 3);
        // return new Order(1, "Larry", Arrays.asList(li));
    }

    public static String serialize(Order order) {
        String text = "ORDER START\n";
        text += "ORDER NUMBER: " + order.getNumber() + "\n";
        text += "CUSTOMER CODE: " + order.getCustomerCode() + "\n";
        for (LineItem li : order) {
            text += "ITEM: " + li.getItem().getCode() + ", " + li.getQuantity() + "\n";
        }
        text += "ORDER END\n";
        return text;

    }


}
