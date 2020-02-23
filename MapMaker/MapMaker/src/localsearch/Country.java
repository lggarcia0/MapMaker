package localsearch;

import java.util.ArrayList;

public class Country {
	State[] states;
	ArrayList<int[]> pairs;
	Country(State[] states, ArrayList<int[]> pairs) {
		this.states = states;
		this.pairs = pairs;
	}
	public State[] getStates() { return this.states; }
	//public boolean[] getPairs() { return this.pairs; }
	public double percentRight() {
		double totalRight = 0.0;
		for (int p = 0; p < pairs.size(); p++) {
			int x = pairs.get(p)[0];
			int y = pairs.get(p)[1];
			if(!this.states[x].conflicts(this.states[y])) {
				totalRight++;
			}
		}
		double total = new Double(pairs.size());
		return (totalRight / total);
	}

    public Country clone()  {
		Country cloned;
		State[] cloneStates = new State[this.states.length];
		for (int s = 0; s < this.states.length; s++) {
			cloneStates[s] = new State(this.states[s].getID(),this.states[s].getName());
			cloneStates[s].setColor(this.states[s].getColor());
			cloneStates[s].neighbors = this.states[s].neighbors;
		}
		cloned = new Country(cloneStates, this.pairs);
		return cloned;
    }
}
