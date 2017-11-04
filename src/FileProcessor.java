import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/** Main Processor for Task */

public class FileProcessor {
	
	public static void main(String[] args) {
		// initialize files and budget
		String drinksFile = "./data/lowUtilityHighHPD-drinks.txt";
		String foodFile = "./data/lowUtilityHighHPD-foods.txt";
		String peopleFile = "./data/people.txt";
		int budget = 15;
		
		// tracks drink & food prices
		HashMap<String,Float> drinksMenu = new HashMap<String,Float>();
		HashMap<String,Float> foodMenu = new HashMap<String,Float>();
		// counts of each drink & food
		HashMap<String,Integer> drinkCount = new HashMap<String,Integer>();
		HashMap<String,Integer> foodCount = new HashMap<String,Integer>();
		// keep list of all guests
		ArrayList<Guest> guestList = new ArrayList<Guest>();
		float unusedMoney = 0;
		
		// setup so program outputs into text file
		PrintStream out = null;
		setupOutputStream(out);
		
		// read text files and input into maps
		readFiles(drinksFile, foodFile, peopleFile, drinksMenu, foodMenu, guestList);
		
		// get budget and calculate how much money each person gets
		int totalBudget = getBudget(budget, args);
		float budgetPerPerson = (float)totalBudget / (float)guestList.size();		
		
		// loop through people and calculate their preferences and update total drink/food counts
		String[] prefs;
		for (Guest guest : guestList) {
			prefs = guest.calcPrefs(budgetPerPerson);
			updateTotalCounts(prefs[0], prefs[1], drinkCount, foodCount);
			unusedMoney = unusedMoney + guest.getUnusedMoney();
		}
		
		// output results
		outputResultsToTextFile(drinkCount, foodCount, guestList, unusedMoney, budgetPerPerson);
		
	}
	
	
	/* Helper Functions used in main processor */
	
	// sets up output stream to text file
	private static void setupOutputStream(PrintStream out) {
		try {
			out = new PrintStream(new FileOutputStream("orders.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	// gets budget from command line arguments, otherwise hard-coded
	private static int getBudget(int hardCodeBudget, String[] args) {
		int totalBudget = hardCodeBudget;
		if (args.length > 0) {
		    try {
		    	totalBudget = Integer.parseInt(args[0]);
		    } catch (NumberFormatException e) {
		        System.err.println("Budget" + args[0] + " must be an integer.");
		        System.exit(1);
		    }
		}
		return totalBudget;
	}
	
	// reads drink and food files into menus, and people file into guest list
	private static void readFiles(String drinksFile, String foodFile, String peopleFile, HashMap<String,Float> drinksMenu, HashMap<String,Float> foodMenu, ArrayList<Guest> guestList) {		
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
	}
	
	// reads drinks or food text files into maps (helper function used in readFiles)
		private static void readTextIntoMap(FileReader file, HashMap<String,Float> map) {
			BufferedReader br = new BufferedReader(file);
			String currentLine;
			String[] parts;
			try{
				while ((currentLine = br.readLine()) != null ) {
					parts = currentLine.replaceAll("\\s","").split(":");
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
					drinks = br.readLine().replaceAll("\\s","").split(",");
					foods = br.readLine().replaceAll("\\s","").split(",");
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

		
		// update map counts of total drinks/foods to buy
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
		public static void outputResultsToTextFile(HashMap<String,Integer> drinkCount, HashMap<String,Integer> foodCount, ArrayList<Guest> guestList, float unusedMoney, float budgetPerPerson) {
			// print total number of each drink/food to order
			System.out.printf("LEFTOVER MONEY: $%.2f\n\n",unusedMoney);
			System.out.println("NUMBER OF EACH DRINK TO ORDER: ");
			drinkCount.forEach((key, value) -> System.out.println(key + ": " + value));
			System.out.println();
			System.out.println("NUMBER OF EACH FOOD TO ORDER: ");
			foodCount.forEach((key, value) -> System.out.println(key + ": " + value));
			System.out.println();
			System.out.printf("Each guest got an alloted: $%.2f \n",budgetPerPerson);
			System.out.println("ORDERS FOR EACH GUEST: ");
			// print individual orders of guests
			for (Guest guest : guestList) {
				System.out.printf("%s: %s, %s \n     Happiness Rating= %.2f / 1.00 \n     Money Spent= $%.2f\n\n",
						guest.name, guest.drinkPref.name, guest.foodPref.name, guest.currentUtility, guest.spent);
			}
		}
		
}
