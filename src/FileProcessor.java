import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/** Main Processor for Task */

public class FileProcessor {
		
	public static void main(String[] args) {
		// initialize files
		String drinksFile = "./data/drinks.txt";
		String foodFile = "./data/food.txt";
		String peopleFile = "./data/people.txt";

		// keep track of drink/food prices
		HashMap<String,Float> drinksMenu = new HashMap<String,Float>();
		HashMap<String,Float> foodMenu = new HashMap<String,Float>();
		// keep track of total counts of each drink/food
		HashMap<String,Integer> drinkCount = new HashMap<String,Integer>();
		HashMap<String,Integer> foodCount = new HashMap<String,Integer>();
		// keep track of all guests
		ArrayList<Guest> guestList = new ArrayList<Guest>();
		
		// output results into text file
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream("orders.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// hard-code total budget or read in as parameter
		int totalBudget = 100;
		if (args.length > 0) {
		    try {
		    	totalBudget = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
		        System.err.println("Budget" + args[0] + " must be an integer.");
		        System.exit(1);
		    }
		}
		
		// read text files
		try {
			FileReader drinksFileReader = new FileReader(drinksFile);
			FileReader foodFileReader = new FileReader(foodFile);
			FileReader peopleFileReader = new FileReader(peopleFile);
		
			// read files into variables
			readTextIntoMap(drinksFileReader,drinksMenu);
			readTextIntoMap(foodFileReader,foodMenu);
			createGuests(peopleFileReader,guestList, drinksMenu, foodMenu);
		}
		catch(FileNotFoundException ex) {
			ex.printStackTrace();
		}
		
		
		// calculate how much money each person gets
		float budgetPerPerson = (float)totalBudget / (float)guestList.size();		
		
		// loop through people and calculate their preferences and update total drink/food counts
		String[] prefs;
		for (Guest guest : guestList) {
			prefs = guest.calcPefs(budgetPerPerson);
			updateTotalCounts(prefs[0], prefs[1], drinkCount, foodCount);
		}
		
		// output results
		outputResultsToTextFile(drinkCount, foodCount, guestList);
		
	}
	
	
	
	
	
	// HELPER FUNCTIONS 
	
	// reads drinks or food text files into maps
		private static void readTextIntoMap(FileReader file, HashMap<String,Float> map) {
			BufferedReader br = new BufferedReader(file);
			String currentLine;
			String[] parts;
			try{
				while ((currentLine = br.readLine()) != null ) {
					parts = currentLine.split(":");
					map.put(parts[0], Float.valueOf(parts[1]));
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// reads text file into array list of guests
		private static void createGuests(FileReader file, ArrayList<Guest> guestList, HashMap<String,Float> drinksMenu, HashMap<String,Float> foodMenu) {
			BufferedReader br = new BufferedReader(file);
			String guestName;
			String[] drinks;
			String[] foods;
			try{
				while ((guestName = br.readLine()) != null ) {
					Guest newGuest = new Guest(guestName);
					drinks = br.readLine().split(",");
					foods = br.readLine().split(",");
					int numDrinkPrefs = drinks.length;
					int numFoodPrefs = foods.length;
					
					int rank = 0;
					String newItemType = "Drink";
					for (String drink : drinks) {
						float utilityRank = (float)1 - ((float)rank / numDrinkPrefs);
						// create item
						Item newDrink = new Item(drink, drinksMenu.get(drink), utilityRank, newItemType);
						newGuest.addPreference(newDrink);
						rank++;
					}
					
					// do same thing for foods
					rank = 0;
					newItemType = "Food";
					for (String food : foods) {
						float utilityRank = 1 - ((float)rank / numFoodPrefs);
						// create item
						Item newFood = new Item(food, foodMenu.get(food), utilityRank, newItemType);
						newGuest.addPreference(newFood);
						rank++;
					}
					
					guestList.add(newGuest);
					
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		
		// update map counts
		public static void updateTotalCounts(String drinkOrder, String foodOrder, HashMap<String,Integer> drinkCount, HashMap<String,Integer> foodCount) {
			// increment the correct drink count
			if (drinkCount.containsKey(drinkOrder)) {
				drinkCount.put(drinkOrder, drinkCount.get(drinkOrder) + 1);
			}
			else {
				drinkCount.put(drinkOrder, 1);
			}
			// increment the correct food count
			if (foodCount.containsKey(foodOrder)) {
				foodCount.put(foodOrder, foodCount.get(foodOrder) + 1);
			}
			else {
				foodCount.put(foodOrder, 1);
			}
		}
		
		
		// output results from algorithm to text file
		public static void outputResultsToTextFile(HashMap<String,Integer> drinkCount, HashMap<String,Integer> foodCount, ArrayList<Guest> guestList) {
			// print total number of each drink/food to order
			System.out.println("Number of DRINKS to order: ");
			drinkCount.forEach((key, value) -> System.out.println(key + ": " + value));
			System.out.println();
			System.out.println("Number of each FOOD to order: ");
			foodCount.forEach((key, value) -> System.out.println(key + ": " + value));
			System.out.println();
			System.out.println("Orders for each guest: ");
			// print individual orders of guests
			for (Guest guest : guestList) {
				System.out.println(guest.name + ": " + guest.drinkPref.name + ", " + guest.foodPref.name);
			}
		}
		
}
