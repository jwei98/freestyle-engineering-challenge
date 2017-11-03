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
	
	// initializer
	public Guest(String name) {
		this.name = name;
	}
	
	// add a drink or food preference to guest's list of preferences
	public void addPreference(Item newPref) {	
		// set guest's initial preferences to the cheapest preferences
		if (newPref.type.equals("Food")) {
			if (newPref.price < foodPref.price) {
				foodPref = newPref;
				return;
			}
		}
		else {
			if (newPref.price < drinkPref.price) {
				drinkPref = newPref;
				return;
			}
		}
		// otherwise just add it to the list
		this.preferences.add(newPref);
	}
	
	// update instance variables related to utility, budget, food/drink preferences
	private void purchase(Item item) {
		float previousUtility;
		if (item.type.equals("Food")) {
			previousUtility = this.foodPref.utilityRank;
			this.foodPref = item;
		}
		else {
			previousUtility = this.drinkPref.utilityRank;
			this.drinkPref = item;
		}
		
		// update guest's current best utility and budget they have left
		this.currentUtility = this.currentUtility - previousUtility + item.utilityRank;
		this.budget = this.budget - this.foodPref.price - this.drinkPref.price;
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
			if (this.budget == 0) {
				break;
			}
			
			// take item with first/highest HPD in list
			itemHighestHPD = this.preferences.remove(0);
			
			// depending on type of item, get the price and utility of the current best item
			if (itemHighestHPD.type.equals("Food")) {
				comparePrice = this.foodPref.price;
				compareUtility = this.foodPref.utilityRank;
			}
			else {
				comparePrice = this.drinkPref.price;
				compareUtility = this.drinkPref.utilityRank;
			}
			
			// check if it's affordable and if it increases my utility. If it does, then purchase it:
			if ((itemHighestHPD.price - comparePrice < this.budget) && (itemHighestHPD.utilityRank > compareUtility)) {
				this.purchase(itemHighestHPD);
			}
		}
		
		// return final preferences
		String[] finalPrefs = {this.drinkPref.name, this.foodPref.name};
		return finalPrefs;
		
	}
	
	// returns money that a guest did not use
	public float getUnusedMoney(int originalBudget) {
		return originalBudget - this.budget;
	}
	
}
