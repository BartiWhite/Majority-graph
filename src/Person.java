import java.util.ArrayList;
import java.util.List;

public class Person {

//	FIELDS
	private final int id;
	private final List<Person> neighbours = new ArrayList<>();
	private Opinion opinion;
	private boolean isZealot = false;
	private boolean isGathered = false;

	private static final List<Person> peopleList = new ArrayList<>();

//	CONSTRUCTORS
	public Person() {
		this.id = peopleList.isEmpty() ? 0 : peopleList.get(peopleList.size() - 1).id + 1;
		peopleList.add(this);
	}

	public Person(Opinion opinion) {
		this();
		this.opinion = opinion;
	}

//	METHODS

//	GETTERS AND SETTERS
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