package panels;

import enums.Mode;
import enums.Opinion;
import utils.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class PopulationController implements Runnable {

	private int populationCount = 20;
	private Double initialFor = 0.5;
	private Double forField = 0.0, againstField = 0.0;
	private Double forChance = 1.0, againstChance = 1.0;
	private int forZealot = 0, againstZealot = 0;
	private volatile boolean isRunning = false;
	private int simulationSpeed = 1;
	private Mode mode = Mode.FLUENT;
	Random random = new Random();
	private final List<Integer> opinions;
	private final BlockingQueue<List<Opinion>> graphQueue, chartQueue;
	private final BlockingQueue<List<Integer>> opinionIndexesQueue;
	private final BlockingQueue<Boolean> controlSimFlow;

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

	public void initPopulation() {
		List<Person> pop = Person.getPeople();
		pop.clear();

		// add people to population with opinion based on parameters
		for (int i = 0; i < this.populationCount; i++) {
			if (i < populationCount * this.getInitialFor()) {
				new Person(Opinion.FOR);
			} else {
				new Person(Opinion.AGAINST);
			}
		}

		// make everybody a neighbor
		for (Person person : pop) {
			for (Person neighbour : pop) {
				if (!person.equals(neighbour)) {
					person.addNeighbour(neighbour);
				}
			}
		}

		// make some a zealot
		for (int i = 0; i < forZealot; i++) {
			int randomInt;

			do {
				randomInt = random.nextInt((int) (populationCount * getInitialFor()));
			} while (pop.get(randomInt).isZealot());

			pop.get(randomInt).setZealot(true);
		}

		for (int i = 0; i < againstZealot; i++) {
			int randomInt;

			do {
				randomInt = (int) (populationCount * getInitialFor()) +
						random.nextInt(pop.size() - (int) (populationCount * getInitialFor()));
			} while (pop.get(randomInt).isZealot());

			pop.get(randomInt).setZealot(true);
		}
	}

	public void nextDay() {
		List<Person> pop = Person.getPeople();
		opinions.clear();
		double chance;

		// gather people for consensus
		int gatheringCount = 3;
		for (int j = 0; j < Math.floor(populationCount / gatheringCount); j++) {
			Person[] gathered = new Person[gatheringCount];
			for (int i = 0; i < gatheringCount; i++) {
				int number;

				do {
					number = random.nextInt(pop.size());
				} while (pop.get(number).isGathered());

				gathered[i] = pop.get(number);
				gathered[i].setGathered(true);
				opinions.add(number);
			}

			int consensus = 0;
			for (Person person : gathered) {
				consensus += person.getOpinion().opinionValue;
			}

			chance = random.nextDouble();
			if (consensus < 0 && chance < againstChance) {
				for (Person person : gathered) {
					person.tryToSetOpinion(Opinion.AGAINST);
				}
			} else if (consensus > 0 && chance < forChance) {
				for (Person person : gathered) {
					person.tryToSetOpinion(Opinion.FOR);
				}
			}
		}

		for (Person person : pop) {
			person.setGathered(false);
		}

		// information field changing minds
		for (Person person : pop) {
			Opinion opinion = person.getOpinion();
			chance = random.nextDouble();
			if (opinion == Opinion.FOR) {
				if (againstField > chance) {
					person.tryToSetOpinion(Opinion.AGAINST);
				}
			} else if (opinion == Opinion.AGAINST) {
				if (forField > chance) {
					person.tryToSetOpinion(Opinion.FOR);
				}
			}
		}
	}

	@Override
	public void run() {
		boolean bool;
		while (true) {
			try {
				if (isRunning) {
					nextDay();
					graphQueue.put(getOpinions());
					chartQueue.put(getOpinions());
					if (mode == Mode.STEPOVER) {
						opinionIndexesQueue.put(opinions);
					}
					if (mode == Mode.FLUENT) {
						Thread.sleep((long) 1000 / simulationSpeed);
					}
					if (mode == Mode.STEPOVER) {
						bool = controlSimFlow.take();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void setParameters(int populationCount, double initialFor, double forField, double againstField,
							  double forChance, double againstChance, int forZealot, int againstZealot,
							  int simulationSpeed) {
		this.populationCount = populationCount;
		this.initialFor = initialFor;
		this.forField = forField;
		this.againstField = againstField;
		this.forChance = forChance;
		this.againstChance = againstChance;
		this.forZealot = forZealot;
		this.againstZealot = againstZealot;
		this.simulationSpeed = simulationSpeed;

		initPopulation();
	}

	public void incrementSpeed() {
		simulationSpeed++;
	}

	public void decrementSpeed() {
		simulationSpeed--;
	}

	public List<Opinion> getOpinions() {
		List<Person> pop = Person.getPeople();
		List<Opinion> opinions = new ArrayList<>();
		for (Person person : pop) {
			opinions.add(person.getOpinion());
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

	public double getInitialFor() {
		return initialFor;
	}
}