
/** Item class stores information about either food or drink item */

public class Item{
	
	public String name = "Generic Item";
	public float price;
	public float utilityRank;
	public float HPD; // Happiness per Dollar
	public String type; // either Food or Drink
	
	// constructor used in guest class to initialize an item of very high price
	// only used so that first added preference takes spot as cheapest preference
	public Item() {
		this.price = Float.MAX_VALUE;
	}
	
	// actual default constructor
	public Item(String name, float price, float utilityRank, String type) {
		this.name = name;
		this.price = price;
		this.utilityRank = utilityRank;
		this.HPD = utilityRank / price;
		this.type = type;
	}
	
	// return negative value of HPD so that custom comparator in Guest class
	// sorts in descending order
	public float getHPD() {
		return this.HPD*-1;
	}
	
}