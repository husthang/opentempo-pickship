package pickship;

import java.util.*;

public class Inventory implements Iterable<Item> {

    private Map<String, Item> items;

    public Inventory(Collection<Item> items) {
        this.items = new HashMap<String, Item>();
        for (Item item : items) {
            this.items.put(item.getCode(), item);
        }
    }

    public Item getItem(String code) {
        return items.get(code);
    }

    public Iterator<Item> iterator() {
        return items.values().iterator();
    }
}