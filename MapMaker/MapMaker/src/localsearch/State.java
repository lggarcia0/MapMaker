package localsearch;

import java.util.ArrayList;

public class State {
	int color;
	int id;
	String name;
	ArrayList<Integer> neighbors;
	
	State(int id, String name) {
		this.color = -1;
		this.id = id;
		this.name = name;
		this.neighbors = new ArrayList<Integer>();
	}
	
	public int getColor() {return this.color; }
	public void setColor(int newColor) {this.color = newColor;}
	public ArrayList<Integer> getNeighbors() { return this.neighbors; }	
	public int getID() {return this.id;}
	public String getName() {return this.name; }
	public void addNeighbor(int neighborID) { this.neighbors.add(neighborID); }
	public boolean conflicts(State another) { // used to see if the color of this state "conflicts" with that of another
		if (this.getColor() == another.getColor()) {
			return true;
		} else {
			return false;
		}
	}
	public double conflictNumber(State[] states) { //used to get the states that is better to change, I eventually didn't use it because it was a bit too complicated to implement
		double conflicts = 0.0;
		for (int n : this.neighbors) {
			if (this.conflicts(states[n])) {
				conflicts++;
			}
		}
		double total = new Double(this.getNeighbors().size());
		return (conflicts / total);
	}
	
}
