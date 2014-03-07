package pickship;

public class Item {

	private String code;
	private String name;
	private double weight;

	public Item(String code, String name, double weight) {
		this.code = code;
		this.name = name;
		this.weight = weight;
	}

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }
}