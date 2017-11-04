import java.util.ArrayList;
import java.util.Comparator;

/** Guest class tracks information regarding a single guest, and
 * contains functionality for calculating a guest's own food/drink preferences */

public class Guest {
	// instance variables
	public String name;
	private float budget;
	private ArrayList<Item> preferences = new ArrayList<Item>();
	public Item foodPref = new Item();
	public Item drinkPref = new Item();
	public float currentUtility = 0;
	public float spent = 0;
	
	// initializer
	public Guest(String name) {
		this.name = name;
	}
	
	// add a drink or food preference to guest's list of preferences
	public void addPreference(Item newPref) {
		// set guest's initial preferences to the cheapest preferences
		if (newPref.type.equals("Food")) {
			if (newPref.price < this.foodPref.price) {
				if (!this.foodPref.name.equals("Generic Item")) {
					this.preferences.add(this.foodPref);
				}
				purchase(newPref);
				return;
			}
		}
		else {
			if (newPref.price < this.drinkPref.price) {
				if (!this.drinkPref.name.equals("Generic Item")) {
					this.preferences.add(this.drinkPref);
				}
				purchase(newPref);
				return;
			}
		}
		// otherwise just add it to the list
		this.preferences.add(newPref);
	}
	
	// update instance variables related to utility, budget, food/drink preferences
	private void purchase(Item item) {
		float previousUtility = 0;
		float previousSpent = 0;
		if (item.type.equals("Food")) {
			previousSpent = this.foodPref.price;
			previousUtility = this.foodPref.utility;
			this.foodPref = item;
		}
		else {
			previousSpent = this.drinkPref.price;
			previousUtility = this.drinkPref.utility;
			this.drinkPref = item;
		}
		
		// for case of first item being purchased
		if (previousSpent == Float.MAX_VALUE) {
			previousSpent = 0;
		}
		
		// update guest's current best utility and budget they have left
		this.currentUtility = this.currentUtility - previousUtility + item.utility;
		this.spent = this.spent - previousSpent + item.price;
	}
	
	
	// calculates optimal (drink, food) combination of a guest given budget
	public String[] calcPrefs(float budget) {
		this.budget = budget;
		
		// sort preferences in descending order of Happiness per Dollar
		this.preferences.sort(Comparator.comparing(Item::getHPD));

		float comparePrice = 0;
		float compareUtility;
		Item itemHighestHPD;
		
		while (true) {
			
			// break if no more preferences to check or have no more money to spend
			if (this.preferences.size() == 0) {
				break;
			}
			if (this.budget == this.spent) {
				break;
			}
			
			// take item with first/highest HPD in list
			itemHighestHPD = this.preferences.remove(0);
			// depending on type of item, get the price and utility of the current best item
			if (itemHighestHPD.type.equals("Food")) {
				comparePrice = this.foodPref.price;
				compareUtility = this.foodPref.utility;
			}
			else {
				comparePrice = this.drinkPref.price;
				compareUtility = this.drinkPref.utility;
			}
			if ((itemHighestHPD.price - comparePrice < this.budget - this.spent) && (itemHighestHPD.utility > compareUtility)) {
				this.purchase(itemHighestHPD);
			}
		}
		
		// return final preferences
		String[] finalPrefs = {this.drinkPref.name, this.foodPref.name};
		return finalPrefs;
		
	}
	
	// returns money that a guest did not use
	public float getUnusedMoney() {
		return this.budget - this.spent;
	}
	
}
