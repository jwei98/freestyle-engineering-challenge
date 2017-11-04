
/** Item class stores information about either food or drink item */

public class Item{
	
	public String name = "Generic Item";
	public float price;
	public float utility = 0;
	public float HPD = 0; // Happiness per Dollar
	public String type = "Generic Type"; // either Food or Drink
	
	// constructor used in guest class to initialize an item of very high price
	// only used so that first added preference takes spot as cheapest preference
	public Item() {
		this.price = Float.MAX_VALUE;
	}
	
	// actual default constructor
	public Item(String name, float price, float utilityRank, String type) {
		this.name = name;
		this.price = price;
		this.type = type;
		// weight drinks as less
		float weighting = (float)2/3;
		if (this.type == "Drink") {
			weighting = (float)1/3;
		}
		this.utility = weighting * utilityRank;
		this.HPD = this.utility / price;
		
	}
	
	// return negative value of HPD so that custom comparator in Guest class
	// sorts in descending order
	public float getHPD() {
		return this.HPD*-1;
	}
	
}