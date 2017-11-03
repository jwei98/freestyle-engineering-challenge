import java.util.ArrayList;
import java.util.Comparator;

/** Guest class tracks information regarding a single guest, and
 * contains functionality for calculating a guest's own food/drink preferences */

public class Guest {
	// instance variables
	public String name;
	private float budget;
	private float spent;
	private ArrayList<Item> preferences = new ArrayList<Item>();
	public Item foodPref = new Item();
	public Item drinkPref = new Item();
	public float currentUtility = 0;
	
	public Guest(String name) {
		this.name = name;
	}
	
	
	public void addPreference(Item newPref) {	
		// update bestFoodPref/bestDrinkPref based on what's cheap
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
		this.preferences.add(newPref);
	}
	
	// spent function takes in positive number of dollars spent
	private void purchase(Item item) {
		System.out.println(this.currentUtility);
		float previousUtility;
		if (item.type.equals("Food")) {
			// modify new utility ranking
			previousUtility = this.foodPref.utilityRank;
			this.foodPref = item;
			
		}
		else {
			previousUtility = this.drinkPref.utilityRank;
			this.drinkPref = item;
		}
		
		this.currentUtility = this.currentUtility - previousUtility + item.utilityRank;
		System.out.println(this.currentUtility);
		this.spent = this.foodPref.price + this.drinkPref.price;
	}
	
	public String[] calcPefs(float budget) {
		this.budget = budget;
		this.spent = this.foodPref.price + this.drinkPref.price;
		
		this.preferences.sort(Comparator.comparing(Item::getHPD));
	
		
		float comparePrice = 0;
		float compareUtility;
		Item itemHighestHPD;
		while (true) {
			// break if no more preferences to check or spent money == budget
			if (this.preferences.size() == 0) {
				break;
			}
			if (this.spent == this.budget) {
				break;
			}
			
			// take first HPD in list
			itemHighestHPD = this.preferences.remove(0);
			
			// get amount I've already spent on either food or drink
			if (itemHighestHPD.type.equals("Food")) {
				comparePrice = this.foodPref.price;
				compareUtility = this.foodPref.utilityRank;
			}
			else {
				comparePrice = this.drinkPref.price;
				compareUtility = this.drinkPref.utilityRank;
			}
			
			// check if affordable and if increases utility, if it does, then purchase it
			if ((itemHighestHPD.price - comparePrice < this.budget - this.spent) && (itemHighestHPD.utilityRank > compareUtility)) {
				this.purchase(itemHighestHPD);
			}
		}
		
		String[] finalPrefs = {this.drinkPref.name, this.foodPref.name};
		return finalPrefs;
	}
	
	public float getUnusedMoney(int originalBudget) {
		return originalBudget - this.budget;
	}
	
}
