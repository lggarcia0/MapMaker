package localsearch;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.ArrayList;

public class LocalSearchImpl {
	static ArrayList<String> colors = new ArrayList<String>();
	static int nodes = 0; // keeps track of the amount of changes made to the solution
	static int restart = 1000; // number of restarts I limited this to so it won't run forever
	static ArrayList<String> stateNames = new ArrayList<String>();
	static State[] states;
	static ArrayList<int[]> relations = new ArrayList<int[]>();
	static Country currentCountry;
	static boolean solved = false;
	
	public static void main(String[] args) {
		try {
			
			boolean goodResponse = false;
			Scanner input;
			File file = new File("src/usTestFile.txt");
			System.out.println("This is the local search map program. ");
			while(!goodResponse) {
				System.out.print("Enter a valid .txt file name with extension : ");
				input = new Scanner(System.in);
				file = new File(input.nextLine());
				if (file.canRead()) {
					goodResponse = true;
				} else {
					goodResponse = false;
				}
				
			}
            input = new Scanner(file);

            String line = null;
            while(!(line = input.nextLine()).isEmpty()) {
            	colors.add(line);
            }
            while(!(line = input.nextLine()).isEmpty()) {
            	stateNames.add(line);
            }
            //Initialize states array
            states = new State[stateNames.size()];
            for (int s = 0; s < stateNames.size(); s++) {
            	states[s] = new State(s, stateNames.get(s));
            }
            // Define Neighbors and Relations
            
            while(input.hasNextLine()) {
            	String[] pair = input.nextLine().split(" ");
            	states[stateNames.indexOf(pair[0])].addNeighbor(stateNames.indexOf(pair[1]));
            	states[stateNames.indexOf(pair[1])].addNeighbor(stateNames.indexOf(pair[0]));
            	int[] pairInt = {stateNames.indexOf(pair[0]), stateNames.indexOf(pair[1])};
            	relations.add(pairInt.clone());
            }


            
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
		LocalSearchImpl LS = new LocalSearchImpl();
		//Create TimeOut Procedure taken from https://stackoverflow.com/questions/5715235/java-set-timeout-on-a-certain-block-of-code
		final Runnable task = new Thread() {
			@Override
			public void run() {
				LS.solve();
			}
		};
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future future = executor.submit(task);
		executor.shutdown();
		try {
			future.get(1, TimeUnit.MINUTES);
		}
		catch (InterruptedException e) {
			
		}
		catch (ExecutionException e){
			
		}
		catch (TimeoutException e) {
			System.out.println("The task has run out of time with no real solution");
		}
		LS.printSolution(solved);
	}
	//Semi-recursive function that relies heavily on a while loop because recursion is not deep enough
	boolean solve() {
		
		boolean pass = true;
		Country[] countries = new Country[1]; // countries array is used to generate an n number of random countries to choose the best one and improve upon it
		for (int possibility = 0; possibility < countries.length; possibility++) {
			for(State s : states) {
				double randy = Math.random(); // randy == random variable
				switch(colors.size()) { // depending on number of colors the colors assigned are split evenly between the states
				case 2:
					if (randy < 0.5) {
						s.setColor(0);
					} else {
						s.setColor(1);
					}
					break;
				case 3:
					if (randy < (1.0 / 3.0)) {
						s.setColor(0);
					} else if (randy < (2.0/3.0)) {
						s.setColor(1);
					} else if (randy >= (2.0/3.0)) {
						s.setColor(2);
					}
					break;
				case 4:
					if (randy < 0.25) {
						s.setColor(0);
					} else if (randy < 0.5) {
						s.setColor(1);
					} else if (randy >= 0.5) {
						s.setColor(2);
					} else if (randy >= 0.75) {
						s.setColor(3);
					}
					break;
				}

			}
			countries[possibility] = new Country(cloneState(states), relations);
			nodes++;
		}
		currentCountry = countries[0];
		for (Country c : countries) { // again to choose the best of the ones generated
			if (c.percentRight() >= currentCountry.percentRight()) {
				currentCountry = c;
			}
		}
		double pastProgress = currentCountry.percentRight(); // initializing how good the current country is to compare with others
		int again = 100; // if the loop has gained no progress in 100 while loops then this will give signal to a recursive call to start again
		if (pastProgress == 1.0) { // if we get lucky and get a winner off the bat no need to enter the while loop
			solved = true;
			return true;
		}
		while(pass) { // to determine if we keep going
			boolean currentPass = false;
			boolean possibleState = false; 
			int randomState = 0;
			while(!possibleState) { //used to search for a possible conflicting state we could change to advance
				double random = Math.random() * new Double(states.length);
				randomState = (int) (random);
				if (currentCountry.states[randomState].conflictNumber(currentCountry.states) > 0) {
					possibleState = true;
				}
			}
			State currentState = currentCountry.states[randomState];
			
			countries = new Country[colors.size()]; // again used to store different branches we can take
			
			for (int color = 0; color < colors.size(); color++) {
				countries[color] = currentCountry.clone(); // clone used to not affect the actual currentCountry
				if(currentState.getColor() != color) {
					countries[color].states[randomState].setColor(color);
				}
			}
			int contendingCountry = currentCountry.states[randomState].getColor();
			for (int cont = 0; cont < countries.length; cont++) { // cont as in country. I know, bad variable name.
				if (currentCountry.states[randomState].getColor() != cont) {// we don't want to change it to the same thing
					if (currentCountry.percentRight() < countries[cont].percentRight() &&  // if this version is better then we do change  it
							countries[contendingCountry].percentRight() < countries[cont].percentRight()) {
						contendingCountry = cont;
					}
					
				}
			}
			if (contendingCountry != currentState.getColor()) {
				currentCountry = countries[contendingCountry];
				nodes++;
				if (currentCountry.percentRight() == 1.0) {
					pass = false;
					solved = true;
				}
			}		
			if (currentCountry.percentRight() == pastProgress) { // if no progress is made
				again--; // then get closer to launching it again
				if (again <= 0) {
					restart--; // and if we do launch it, keep track of it so it doesn't do it forever
					again = 100;
					if (restart > 0) {
						if (solve()) { // recursive call to do again
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				}
			} else {
				again = 100; // if not the same, our again variable resets
				pastProgress = currentCountry.percentRight(); // and we update the pastProgress to the current one
			}
			
		}
		return true; 
	}
	void printSolution(boolean solved) { 
		if (solved) {
			for (State s : currentCountry.states) {
				String output = s.getName() + " " + colors.get(s.getColor());
				System.out.print(output);
				System.out.println();
			}
		} else {
			System.out.println((currentCountry.percentRight() * 100.0) + " percent done"); // used to see how close we got before it timed out
		}
		System.out.println(nodes);
		
    } 

	State[] cloneState(State[] original) { // to clone the states when we don't have a country to clone
		State[] cloneStates = new State[original.length];
		for (int s = 0; s < original.length; s++) {
			cloneStates[s] = new State(original[s].getID(),original[s].getName());
			cloneStates[s].setColor(original[s].getColor());
			cloneStates[s].neighbors = original[s].neighbors;
		}
		return cloneStates;
	}
	
	
	
}
