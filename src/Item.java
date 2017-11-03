
/** Item class stores information about either food or drink item */

public class Item{
	
	public String name = "Generic Item";
	public float price;
	public float utilityRank;
	public float HPD; // Happiness per Dollar
	public String type;
	
	public Item() {
		this.price = Float.MAX_VALUE;
	}
	
	public Item(String name, float price, float utilityRank, String type) {
		this.name = name;
		this.price = price;
		this.utilityRank = utilityRank;
		this.HPD = utilityRank / price;
		this.type = type;
	}
	
	public float getHPD() {
		return this.HPD*-1;
	}
}