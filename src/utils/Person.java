package utils;

import enums.Opinion;

import java.util.ArrayList;
import java.util.List;

public class Person {

	private static final List<Person> peopleList = new ArrayList<>();

	private final List<Person> neighbours = new ArrayList<>();
	private Opinion opinion;
	private boolean isZealot = false;
	private boolean isGathered = false;

	public Person() {
		peopleList.add(this);
	}

	public Person(Opinion opinion) {
		this();
		this.opinion = opinion;
	}

	public void addNeighbour(Person neighbour) {
		this.neighbours.add(neighbour);
	}

	public Opinion getOpinion() {
		return opinion;
	}

	public void tryToSetOpinion(Opinion opinion) {
		if (!isZealot) {
			this.opinion = opinion;
		}
	}

	public static List<Person> getPeople() {
		return peopleList;
	}

	public boolean isZealot() {
		return isZealot;
	}

	public void setZealot(boolean isZealot) {
		this.isZealot = isZealot;
	}

	public boolean isGathered() {
		return isGathered;
	}

	public void setGathered(boolean isGathered) {
		this.isGathered = isGathered;
	}

}