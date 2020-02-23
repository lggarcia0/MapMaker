package backtracking;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;

public class BacktrackingImpl {
	static ArrayList<String> colors = new ArrayList<String>();
	static int nodes = 0;
	static ArrayList<String> stateNames = new ArrayList<String>();
	static State states[];
	static int order[];
	
	public static void main(String[] args) {
		try {
			boolean goodResponse = false;
			Scanner input;
			File file = new File("src/usTestFile.txt");
			System.out.println("This is the back tracking search map program. ");
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
            boolean[] stateColors = new boolean[colors.size()];
            for (int i = 0; i < colors.size(); i++) {
            	stateColors[i] = true;
            }
            while(!(line = input.nextLine()).isEmpty()) {
            	stateNames.add(line);
            }
            //Initialize states array
            states = new State[stateNames.size()];
            for (int s = 0; s < stateNames.size(); s++) {
            	states[s] = new State(s, stateNames.get(s), stateColors.clone());
            }
            // Define Neighbors
            while(input.hasNextLine()) {
            	String[] pair = input.nextLine().split(" ");
            	states[stateNames.indexOf(pair[0])].addNeighbor(stateNames.indexOf(pair[1]));
            	states[stateNames.indexOf(pair[1])].addNeighbor(stateNames.indexOf(pair[0]));
            }
            //Create Optimal Order
            HashMap<Integer, ArrayList<Integer>> neighborNum = new HashMap<Integer, ArrayList<Integer>>();

            int currentMax = 0;
            for (int s = 0; s < stateNames.size(); s++) {
            	int num = states[s].getNeighbors().size();
            	if (currentMax < num) {
            		currentMax = num;
            	}
            	if (!neighborNum.containsKey(num)) {
            		neighborNum.put(new Integer(num), new ArrayList<Integer>());
            	}
            	neighborNum.get(new Integer(states[s].getNeighbors().size())).add(new Integer(s));
            }
            order = new int[stateNames.size()];
            for (int o = 0; o < stateNames.size(); o++) {
            	if (!(neighborNum.get(currentMax).isEmpty())) {
            		order[o] = neighborNum.get(currentMax).get(0);
            		neighborNum.get(currentMax).remove(0);
            	} else {
            		if (currentMax > 0) {
            			currentMax--;
            		}
            		while ((currentMax > 0) && !(neighborNum.containsKey(currentMax))) {
            			currentMax--;
            		}
            		if (!(neighborNum.get(currentMax).isEmpty())) {
                		order[o] = neighborNum.get(currentMax).get(0);
                		neighborNum.get(currentMax).remove(0);
                	}
            	}
            }
            
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
		BacktrackingImpl BT = new BacktrackingImpl();
		BT.solve(0);
		BT.printSolution();
	}
	//Recursion function to test and place all possible variables
	boolean solve(int index) {
		if (index >= states.length) { // if the index is greater than the possible length we're done
			return true;
		}

		State current = states[order[index]]; // instead of going with the order given, use the optimal order to increase efficiency
		for (int color = 0; color < current.getPossColors().length; color++) { // now to iterate over which colors work for a state
			
			if (current.getPossColors()[color]) {
				boolean[] wasTrue = new boolean[current.getNeighbors().size()]; // used to keep track of which colors the neighbors could choose before this
				boolean possible = true;
				for (int n = 0; n < current.getNeighbors().size(); n++) { // before we set we'll see if the current States neighbors will be okay without using the color
					State neighbor = states[current.getNeighbors().get(n)];
					if (neighbor.getPossColors()[color]) {
						wasTrue[n] = true; 
						neighbor.removePossibility(color);
					} else {
						wasTrue[n] = false;
					}
				}
				for (int n = 0; n < current.getNeighbors().size(); n++) {
					State neighbor = states[current.getNeighbors().get(n)];
					if (!checkPossibility(neighbor)) {
						possible = false;
					}
				}
				if(possible) { //if still possible we set 
					current.setColor(color);
					this.nodes++;
					if(solve(index+1)) {
						return true;
					} else {
						current.setColor(-1);
						for (int n = 0; n < current.getNeighbors().size(); n++) {
							State neighbor = states[current.getNeighbors().get(n)];
							if (wasTrue[n]) {
								neighbor.restorePossibility(color);
							}
						}
					}
				} else {
					for (int n = 0; n < current.getNeighbors().size(); n++) { // used to revert the changes to the neighbors possible colors
						State neighbor = states[current.getNeighbors().get(n)];
						if (wasTrue[n]) {
							neighbor.restorePossibility(color);
						}
					}
				}
			}
		}
		return false;
	}
	void printSolution() { 
		for (State s : states) {
			String output = s.getName() + " " + colors.get(s.getColor());
			System.out.print(output);
			System.out.println();
		}
		System.out.println(nodes);
    } 
	//Checking  there is at least one possible color
	boolean checkPossibility(State current) {
		for(boolean color : current.getPossColors()) if(color) return true;
	    return false;
	}
	
	
}
