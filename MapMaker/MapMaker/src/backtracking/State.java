package backtracking;

import java.util.ArrayList;

public class State {
	int color;
	int id;
	String name;
	ArrayList<Integer> neighbors;
	boolean[] possibleColors;
	State(int id, String name, boolean[] possColors) {
		this.color = -1;
		this.id = id;
		this.name = name;
		this.neighbors = new ArrayList<Integer>();
		this.possibleColors = possColors;
	}
	
	public int getColor() {return this.color; }
	public void setColor(int newColor) {this.color = newColor;}
	public ArrayList<Integer> getNeighbors() { return this.neighbors; }	
	public boolean[] getPossColors() { return this.possibleColors;}
	public void removePossibility(int color) {this.possibleColors[color] = false;}
	public void restorePossibility(int color) {this.possibleColors[color] = true;}
	public int getID() {return this.id;}
	public String getName() {return this.name; }
	public void addNeighbor(int neighborID) { this.neighbors.add(neighborID); }
	
}
