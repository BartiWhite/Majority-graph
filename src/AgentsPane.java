import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import javax.swing.JPanel;

public class AgentsPane extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	private final List<Circle> circles;
	private List<Opinion> opinions;
	private List<Integer> opinionIndexes;
	private final BlockingQueue<List<Opinion>> graphQueue;
	private final BlockingQueue<List<Integer>> opinionIndexesQueue;
	private final BlockingQueue<Boolean> nextStep, controlSimFlow, updateChart;
	private final List<Integer> coordinates;
	private int numberOfAgents = 20, index1 = 0, index2 = 0, index3 = 0, simulationSpeed = 1;
	private double initFor = 0.5;
	private boolean simulation = false, simulationStop = false, newSimulation = false;
	private Mode mode = Mode.FLUENT;
	private lineColor coloredLine = lineColor.BLACKLINE;

	private enum lineColor {
		BLACKLINE, REDLINE, BLUELINE
	}

	public AgentsPane(BlockingQueue<List<Opinion>> graphQueue, BlockingQueue<List<Integer>> opinionIndexesQueue,
			BlockingQueue<Boolean> nextStep, BlockingQueue<Boolean> controlSimFlow,
			BlockingQueue<Boolean> updateChart) {
		this.setPreferredSize(new Dimension(640, 400));
		this.graphQueue = graphQueue;
		this.opinionIndexesQueue = opinionIndexesQueue;
		this.nextStep = nextStep;
		this.controlSimFlow = controlSimFlow;
		this.updateChart = updateChart;

		coordinates = new ArrayList<>();
		circles = new ArrayList<>();
		opinions = new ArrayList<>();
		opinionIndexes = new ArrayList<>();
		int popPart = (int) Math.round(initFor * numberOfAgents);
		for (int i = 0; i < numberOfAgents; i++)
			if (i < popPart)
				opinions.add(Opinion.FOR);
			else
				opinions.add(Opinion.AGAINST);
		setCoordinates();
	}

	@Override
	public void run() {
		while (true) {

			try {
				opinions = graphQueue.take();
				if (mode == Mode.STEPOVER)
					opinionIndexes = opinionIndexesQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			updateCircles();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		drawLines(g2d);

		for (Circle circle : circles)
			circle.draw(g2d);
	}

	public void drawLines(Graphics2D g2d) {

		if (coloredLine == lineColor.BLACKLINE)
			g2d.setColor(Color.black);
		else if (coloredLine == lineColor.REDLINE)
			g2d.setColor(Color.red);
		else if (coloredLine == lineColor.BLUELINE)
			g2d.setColor(Color.blue);

		if (mode == Mode.STEPOVER && index1 != -1) {
			g2d.drawLine(circles.get(index1).getX() + 8, circles.get(index1).getY() + 8, circles.get(index2).getX() + 8,
					circles.get(index2).getY() + 8);
			g2d.drawLine(circles.get(index1).getX() + 8, circles.get(index1).getY() + 8, circles.get(index3).getX() + 8,
					circles.get(index3).getY() + 8);
			g2d.drawLine(circles.get(index3).getX() + 8, circles.get(index3).getY() + 8, circles.get(index2).getX() + 8,
					circles.get(index2).getY() + 8);
		}
	}

	private void updateCircles() {
		Color color = new Color(0);
		if (mode == Mode.STEPOVER) {
			Opinion opinion;

			for (int i = 0; i < opinionIndexes.size(); i += 3) {
				if(mode != Mode.STEPOVER)
					break;
				index1 = opinionIndexes.get(i);
				index2 = opinionIndexes.get(i + 1);
				index3 = opinionIndexes.get(i + 2);
				opinion = opinions.get(index1);

				coloredLine = lineColor.BLACKLINE;

				if(i == 0)
					repaint();

				if(!simulation)
				{
					try {
						Boolean receiveMessage = nextStep.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(breakSimulation())
					break;
				if (mode == Mode.FLUENT)
					break;

				repaint();

				if (opinion.opinionValue == 1) {
					color = Color.blue;
					coloredLine = lineColor.BLUELINE;
				} else if (opinion.opinionValue == -1) {
					color = Color.red;
					coloredLine = lineColor.REDLINE;
				}
				circles.get(index1).setColor(color);
				circles.get(index2).setColor(color);
				circles.get(index3).setColor(color);

				circles.get(index1).setOvalColor(Color.green);
				circles.get(index2).setOvalColor(Color.green);
				circles.get(index3).setOvalColor(Color.green);

				if (!simulation) {
					try {
						Boolean receiveMessage = nextStep.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(breakSimulation())
					break;

				if (mode == Mode.FLUENT)
					break;

				if (simulationStop) {
					try {
						Boolean receiveMessage = nextStep.take();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				repaint();

				if (simulation)
					try {
						Thread.sleep((long) 1000 / simulationSpeed);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				if(index1 == -1)
				{
					for(Circle circle : circles)
						circle.setOvalColor(Color.black);
				}
				else
				{
					circles.get(index1).setOvalColor(Color.black);
					circles.get(index2).setOvalColor(Color.black);
					circles.get(index3).setOvalColor(Color.black);
				}
			}
		} else {
			for (int i = 0; i < numberOfAgents; i++) {
				if (opinions.get(i).opinionValue == 1)
					color = Color.blue;
				else {
					color = Color.red;
				}
				circles.get(i).setColor(color);
			}
			repaint();
		}

		if (mode == Mode.STEPOVER) {
			try {
				updateChart.put(true);
				controlSimFlow.put(true);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void setCoordinates() {
		int x;
		Random rand = new Random();
		Color color;
		for (int i = 0; i < numberOfAgents; i++) {
			x = rand.nextInt(1000);
			do {
				x = (x + 1) % 1000;
			} while (coordinates.contains(x));
			coordinates.add(x);

			if (opinions.get(i).opinionValue == 1)
				color = Color.blue;
			else {
				color = Color.red;
			}

			circles.add(new Circle((x % 40) * 16, (x / 40) * 16, 16, color));
		}
	}

	private boolean breakSimulation()
	{
		if(newSimulation)
		{
			newSimulation = false;
			return true;
		}
		return false;
	}

	public void setParameters(int numberOfAgents, double initFor) {
		this.numberOfAgents = numberOfAgents;
		this.initFor = initFor;
	}

	public void update() {
		circles.clear();
		coordinates.clear();
		opinions.clear();
		index1 = -1;

		int popPart = (int) Math.round(initFor * numberOfAgents);
		for (int i = 0; i < numberOfAgents; i++)
			if (i < popPart)
				opinions.add(Opinion.FOR);
			else
				opinions.add(Opinion.AGAINST);

		setCoordinates();
		repaint();
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Mode getMode() { return mode;}

	public void setSimulation(boolean simulation) {
		this.simulation = simulation;
	}

	public boolean getSimulation() {return simulation;}

	public void setSimulationStop(boolean simulationStop) {
		this.simulationStop = simulationStop;
	}

	public void setNewSimulation(boolean newSimulation) {
		this.newSimulation = newSimulation;
	}

	public void setSpeed(int simulationSpeed) {
		this.simulationSpeed = simulationSpeed;
	}
}
