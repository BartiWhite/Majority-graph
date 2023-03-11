import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class PopulationController implements Runnable {

	// FIELDS
	private int populationCount = 20;
	private double initialFor = 0.5;
	private double forField = 0.0, againstField = 0.0;
	private double forChance = 1, againstChance = 1;
	private int forZealot = 0, againstZealot = 0;
	private int gatheringCount = 3;
	private volatile boolean isRunning = false;
	private int simulationSpeed = 1;
	private Mode mode = Mode.FLUENT;
	Random random = new Random();
	private List<Integer> opinions = new ArrayList<>();
	private BlockingQueue<List<Opinion>> graphQueue, chartQueue;
	private BlockingQueue<List<Integer>> opinionIndexesQueue;
	private BlockingQueue<Boolean> controlSimFlow;

// CONSTRUCTORS
	public PopulationController(BlockingQueue<List<Opinion>> graphQueue,
			BlockingQueue<List<Integer>> opinionIndexesQueue, BlockingQueue<List<Opinion>> chartQueue,
			BlockingQueue<Boolean> controlSimFlow) {
		this.graphQueue = graphQueue;
		this.opinionIndexesQueue = opinionIndexesQueue;
		this.chartQueue = chartQueue;
		this.controlSimFlow = controlSimFlow;
		this.opinions = new ArrayList<>();
		initPopulation();
	}

//	METHODS

	public void initPopulation() {
		List<Person> pop = Person.getPeople();
		pop.clear();

		// add people to population with opinion based on parameters
		for (int i = 0; i < (int) this.populationCount; i++) {
			if (i < populationCount * this.getInitialFor()) {
				new Person(Opinion.FOR);
			} else {
				new Person(Opinion.AGAINST);
			}
		}

		// make everybody a neighbor ( to be changed in a different society model)
		for (Person person : pop) {
			for (Person neighbour : pop) {
				if (!person.equals(neighbour))
					person.addNeighbour(neighbour);
			}
		}

		// make some a zealot
		assert againstZealot <= populationCount * (1 - initialFor);
		assert forZealot <= populationCount * initialFor;
		for (int i = 0; i < forZealot; i++) {
			int randomInt = random.nextInt((int) (populationCount * getInitialFor()));
//			random.nextInt((int) (populationCount * getInitialFor()));
			if (!pop.get(randomInt).isZealot()) {
				pop.get(randomInt).setZealot(true);
			} else
				i--;
		}
		for (int i = 0; i < againstZealot; i++) {
			int randomInt = (int) (populationCount * getInitialFor()) +  random.nextInt(pop.size() - (int) (populationCount * getInitialFor()));
			if (!pop.get(randomInt).isZealot()) {
				pop.get(randomInt).setZealot(true);
			} else {
				i--;
			}
		}
	}

	public void nextDay() {
		List<Person> pop = Person.getPeople();
		opinions.clear();
		double chance;

		// gather people for consensus
		for (int j = 0; j < Math.floor(populationCount / gatheringCount); j++) {
			Person[] gathered = new Person[gatheringCount];
			for (int i = 0; i < this.gatheringCount; i++) {
				int number = random.nextInt(pop.size());
				if (!pop.get(number).isGathered()) {
					gathered[i] = pop.get(number);
					gathered[i].setGathered(true);
					opinions.add(number);
				} else {
					i--;
				}
			}

			int consensus = 0;
			for (Person pers : gathered) {
				consensus += pers.getOpinion().opinionValue;
			}
			assert consensus != 0;
			chance = random.nextDouble();
			if (consensus < 0 && chance < againstChance) {
				for (Person pers : gathered) {
					pers.tryToSetOpinion(Opinion.AGAINST);
				}
			} else if (consensus > 0 && chance < forChance) {
				for (Person pers : gathered) {
					pers.tryToSetOpinion(Opinion.FOR);
				}
			}
		}

		for (Person pers : pop) {
			pers.setGathered(false);
		}

		// information field changing minds
		for (Person pers : pop) {
			Opinion opinion = pers.getOpinion();
			chance = random.nextDouble();
			if (opinion == Opinion.FOR) {
				if (againstField > chance) {
					pers.tryToSetOpinion(Opinion.AGAINST);
				}
			} else if (opinion == Opinion.AGAINST) {
				if (forField > chance) {
					pers.tryToSetOpinion(Opinion.FOR);
				}
			}
		}
	}

	public void printoutState() {
		for (Person pers : Person.getPeople()) {
			if (pers.getOpinion() == Opinion.AGAINST)
				System.out.print('-');
			if (pers.getOpinion() == Opinion.FOR)
				System.out.print('+');
		}
		System.out.println();
	}

	public boolean isFinalState() {
		List<Person> pop = Person.getPeople();
		boolean possibleFinish = true;
		Opinion possibleOpinion = pop.get(0).getOpinion();

		if (possibleOpinion == Opinion.AGAINST) {
			for (Person pers : pop) {
				if (pers.getOpinion() == Opinion.FOR) {
					possibleFinish = false;
					break;
				}
			}
		} else if (possibleOpinion == Opinion.FOR) {
			for (Person pers : pop) {
				if (pers.getOpinion() == Opinion.AGAINST) {
					possibleFinish = false;
					break;
				}
			}
		}

		if (possibleFinish) {
			if (possibleOpinion == Opinion.FOR) {
				System.out.printf("%.2f", this.initialFor);
				System.out.println(".\t\t\t+++++++++++");
			} else if (possibleOpinion == Opinion.AGAINST) {
				System.out.printf("%.2f", this.initialFor);
				System.out.println(".\t\t\t----------");
			}
		}

		return possibleFinish;

	}

	@Override
	public void run() {
		while (true) {
			try {
				if (isRunning) {
					nextDay();
					graphQueue.put(getOpinions());
					chartQueue.put(getOpinions());
					if (mode == Mode.STEPOVER) {
						opinionIndexesQueue.put(opinions);
					}
					if (mode == Mode.FLUENT)
						Thread.sleep((long) 1000 / simulationSpeed);
					Boolean bool;
					if (mode == Mode.STEPOVER)
						bool = controlSimFlow.take();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void runSociety() {
		while (!isFinalState()) {
			printoutState();
			nextDay();
		}
	}

	public void setParameters(int populationCount, double initialFor, double forField, double againstField,
							  int simulationSpeed) {
		this.populationCount = populationCount;
		this.initialFor = initialFor;
		this.forField = forField;
		this.againstField = againstField;
		this.simulationSpeed = simulationSpeed;

		initPopulation();
	}

	public void incrementSpeed() {
		simulationSpeed++;
	}

	public void decrementSpeed() {
		simulationSpeed--;
	}

	public void incrementInitialFor() {
		initialFor += 0.01;
	}

	public void decrementInitialFor() {
		initialFor -= 0.01;
	}

	public void incrementForField() {
		forField += 0.01;
	}

	public void decrementForField() {
		forField -= 0.01;
	}

	public void incrementAgainstField() {
		againstField += 0.01;
	}

	public void decrementAgainstField() {
		againstField -= 0.01;
	}

//	GETTERS AND SETTERS

	public List<Opinion> getOpinions() {
		List<Person> pop = Person.getPeople();
		List<Opinion> opinions = new ArrayList<>();
		for (int i = 0; i < pop.size(); i++) {
			opinions.add(pop.get(i).getOpinion());
		}
		return opinions;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setSpeed(int simulationSpeed) {
		this.simulationSpeed = simulationSpeed;
	}

	public void setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean getIsRunning() {
		return isRunning;
	}

	public int getSpeed() {
		return simulationSpeed;
	}

	public double getForField() {
		return forField;
	}

	public void setForField(double forField) {
		this.forField = forField;
	}

	public double getAgainstField() {
		return againstField;
	}

	public void setAgainstField(double againstField) {
		this.againstField = againstField;
	}

	public double getForChance() {
		return forChance;
	}

	public void setForChance(double forChance) {
		this.forChance = forChance;
	}

	public double getAgainstChance() {
		return againstChance;
	}

	public void setAgainstChance(double againstChance) {
		this.againstChance = againstChance;
	}

	public int getForZealot() {
		return forZealot;
	}

	public void setForZealot(int forZealot) {
		this.forZealot = forZealot;
	}

	public int getAgainstZealot() {
		return againstZealot;
	}

	public void setAgainstZealot(int againstZealot) {
		this.againstZealot = againstZealot;
	}

	public double getInitialFor() {
		return initialFor;
	}

	public void setInitialFor(double initialFor) {
		this.initialFor = initialFor;
	}

	public Mode getMode() {
		return mode;
	}
}