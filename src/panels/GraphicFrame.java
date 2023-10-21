package panels;

import enums.Mode;
import enums.Opinion;
import utils.Button;
import utils.TextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;

public class GraphicFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static PopulationController pop;
	private static AgentsPane agentsPane;
	private static ChartPane chartPane;
	private final JTabbedPane tabbedPane;
	private final BlockingQueue<List<Opinion>> graphQueue = new ArrayBlockingQueue<>(5);
	private final BlockingQueue<List<Opinion>> chartQueue = new ArrayBlockingQueue<>(5);
	private final BlockingQueue<List<Integer>> opinionIndexesQueue = new ArrayBlockingQueue<>(5);
	private final BlockingQueue<Boolean> nextStep = new ArrayBlockingQueue<>(1),
			controlSimFlow = new ArrayBlockingQueue<>(1),
			updateChart = new ArrayBlockingQueue<>(1);

	private final List<Queue> queuesList = new ArrayList<>(List.of(graphQueue, chartQueue, opinionIndexesQueue,
			nextStep, controlSimFlow, updateChart));

	private final JButton start, stop, reset, stepOverStart, stepOverStop, stepOverReset, simulateButton, stepOverButton, manual;

	private final Button agentUp, agentDown, initForDown, initForUp, speedUp, speedDown, forFieldButtonUp, forFieldButtonDown,
			againstFieldButtonUp, againstFieldButtonDown, forZealotButtonUp, forZealotButtonDown,
			againstZealotButtonUp, againstZealotButtonDown, forChanceButtonUp, forChanceButtonDown,
			againstChanceButtonUp, againstChanceButtonDown, stepOverSpeedUp, stepOverSpeedDown;

	private final TextField populationCountTextField, speedTextField, stepOverSpeedTextField, initForTextField,
			forFieldTextField, againstFieldTextField, forZealotTextField, againstZealotTextField,
			forChanceTextField, againstChanceTextField;

	private final List<TextField> textFields = new ArrayList<>();

	private final JPanel leftPanel, mainButtonsPanel, mainStepOverButtonPanel, populationCountButtonPanel,
			fluentSpeedButtonPanel, stepOverSpeedButtonPanel, initForPanel, forFieldPanel, againstFieldPanel,
			forZealotPanel, againstZealotPanel, forChancePanel, againstChancePanel;

	private final JLabel speedLabel1, speedLabel2, populationCountLabel, initForLabel, forFieldLabel, againstFieldLabel,
			forZealotLabel, againstZealotLabel, forChanceLabel, againstChanceLabel;

	private Integer numberOfAgents = 20, forZealots = 0, againstZealots = 0;

	private Double initialFor = 0.5, forField = 0., againstField = 0., forChance = 1.0, againstChance = 1.0;

	public GraphicFrame() {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("MajorityGraph");
		this.setSize(900, 760);
		this.setMinimumSize(new Dimension(900, 760));
		this.setLayout(new BorderLayout());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				pop.setIsRunning(false);
				System.exit(0);
			}
		});

		// Queues
		pop = new PopulationController(graphQueue, opinionIndexesQueue, chartQueue, controlSimFlow);
		agentsPane = new AgentsPane(graphQueue, opinionIndexesQueue, nextStep, controlSimFlow, updateChart);
		chartPane = new ChartPane(chartQueue, updateChart);

		// JButtons
		manual = new JButton("Manual");
		leftPanel = new JPanel();
		start = new JButton("Start");
		stop = new JButton("Stop");
		reset = new JButton("Reset");
		stepOverStart = new JButton("Start");
		stepOverStop = new JButton("Stop");
		stepOverReset = new JButton("Reset");
		List<JButton> mainButtons = new ArrayList<>(List.of(start, stop, reset, stepOverStart, stepOverStop, stepOverReset));
		simulateButton = new JButton("Simulate");
		stepOverButton = new JButton("Next step");

		// JPanels
		mainButtonsPanel = new JPanel();
		mainStepOverButtonPanel = new JPanel();
		populationCountButtonPanel = new JPanel();
		fluentSpeedButtonPanel = new JPanel();
		stepOverSpeedButtonPanel = new JPanel();
		initForPanel = new JPanel();
		forFieldPanel = new JPanel();
		againstFieldPanel = new JPanel();
		forZealotPanel = new JPanel();
		againstZealotPanel = new JPanel();
		forChancePanel = new JPanel();
		againstChancePanel = new JPanel();

		// Labels
		populationCountLabel = new JLabel("Manage nr. of agents", SwingConstants.CENTER);
		speedLabel1 = new JLabel("Set speed of simulation", SwingConstants.CENTER);
		speedLabel2 = new JLabel("Set speed of simulation", SwingConstants.CENTER);
		initForLabel = new JLabel("Set initial nr. of for opinions", SwingConstants.CENTER);
		forFieldLabel = new JLabel("Set for field", SwingConstants.CENTER);
		againstFieldLabel = new JLabel("Set against field", SwingConstants.CENTER);
		forZealotLabel = new JLabel("Set for zealots number", SwingConstants.CENTER);
		againstZealotLabel = new JLabel("Set against zealots number", SwingConstants.CENTER);
		forChanceLabel = new JLabel("Set for chance", SwingConstants.CENTER);
		againstChanceLabel = new JLabel("Set against chance", SwingConstants.CENTER);

		// TextFields
		populationCountTextField = new TextField(numberOfAgents, "20", mainButtons, 0, 1000);
		initForTextField = new TextField(initialFor, "0.5", mainButtons, 0, 1);
		forFieldTextField = new TextField(forField, "0", mainButtons, 0, 1);
		againstFieldTextField = new TextField(againstField, "0", mainButtons, 0, 1);
		forZealotTextField = new TextField(forZealots, "0", mainButtons, 0, 1000);
		againstZealotTextField = new TextField(againstZealots, "0", mainButtons, 0, 1000);
		forChanceTextField = new TextField(forChance, "1", mainButtons, 0, 1);
		againstChanceTextField = new TextField(againstChance, "1", mainButtons, 0, 1);
		speedTextField = new TextField(pop, "1", mainButtons,0, 100);

		stepOverSpeedTextField = new TextField(pop, "1", mainButtons,0, 100);

		textFields.addAll(List.of(populationCountTextField, speedTextField, stepOverSpeedTextField, initForTextField,
				forFieldTextField, againstFieldTextField, forZealotTextField, againstZealotTextField,
				forChanceTextField, againstChanceTextField));

		// Buttons
		agentUp = new Button("Up", populationCountTextField, 3, 1000, 10);
		agentDown = new Button("Down", populationCountTextField, 3, 1000, 10);
		initForUp = new Button("Up", initForTextField, 0, 1, 0.01);
		initForDown = new Button("Down", initForTextField, 0, 1, 0.01);
		forFieldButtonDown = new Button("Down", forFieldTextField, 0, 1, 0.01);
		forFieldButtonUp = new Button("Up", forFieldTextField, 0, 1, 0.01);
		againstFieldButtonDown = new Button("Down", againstFieldTextField, 0, 1, 0.01);
		againstFieldButtonUp = new Button("Up", againstFieldTextField, 0, 1, 0.01);
		speedUp = new Button("Up", pop, speedTextField, 1, 100);
		speedDown = new Button("Down", pop, speedTextField, 1, 100);
		forZealotButtonUp = new Button("Up", forZealotTextField, 0, 1000, 1);
		forZealotButtonDown = new Button("Down", forZealotTextField, 0, 1000, 1);
		againstZealotButtonUp = new Button("Up", againstZealotTextField, 0, 1000, 1);
		againstZealotButtonDown = new Button("Down", againstZealotTextField, 0, 1000, 1);
		forChanceButtonUp = new Button("Up", forChanceTextField, 0, 1, 0.01);
		forChanceButtonDown = new Button("Down", forChanceTextField, 0, 1, 0.01);
		againstChanceButtonUp = new Button("Up", againstChanceTextField, 0, 1, 0.01);
		againstChanceButtonDown = new Button("Down", againstChanceTextField, 0, 1, 0.01);

		stepOverSpeedUp = new Button("Up", pop, speedTextField, 1, 100);
		stepOverSpeedDown = new Button("Down", pop, speedTextField, 1, 100);

		// utils.TextField inject buttons
		populationCountTextField.addButtons(List.of(agentUp, agentDown));
		speedTextField.addButtons(List.of(speedUp, speedDown));
		initForTextField.addButtons(List.of(initForUp, initForDown));
		forFieldTextField.addButtons(List.of(forFieldButtonUp, forFieldButtonDown));
		againstFieldTextField.addButtons(List.of(againstFieldButtonUp, againstFieldButtonDown));
		forZealotTextField.addButtons(List.of(forZealotButtonUp, forZealotButtonDown));
		againstZealotTextField.addButtons(List.of(againstZealotButtonUp, againstZealotButtonDown));
		forChanceTextField.addButtons(List.of(forChanceButtonUp, forChanceButtonDown));
		againstChanceTextField.addButtons(List.of(againstChanceButtonUp, againstChanceButtonDown));

		stepOverSpeedTextField.addButtons(List.of(stepOverSpeedUp, stepOverSpeedDown));

		// TabbedPane
		tabbedPane = new JTabbedPane();

		initGUI();
		initListeners();

		Thread graphThread = new Thread(agentsPane);
		Thread charThread = new Thread(chartPane);
		Thread controllerThread = new Thread(pop);
		graphThread.start();
		charThread.start();
		controllerThread.start();
	}

	private void initGUI() {
		leftPanel.setLayout(new GridLayout(16, 1));

		// filling left panel
		fillPanel(populationCountButtonPanel, populationCountTextField, agentDown, agentUp, populationCountLabel);
		fillPanel(initForPanel, initForTextField, initForDown, initForUp, initForLabel);
		fillPanel(forFieldPanel, forFieldTextField, forFieldButtonDown, forFieldButtonUp, forFieldLabel);
		fillPanel(againstFieldPanel, againstFieldTextField, againstFieldButtonDown, againstFieldButtonUp, againstFieldLabel);
		fillPanel(forZealotPanel, forZealotTextField, forZealotButtonDown, forZealotButtonUp, forZealotLabel);
		fillPanel(againstZealotPanel, againstZealotTextField, againstZealotButtonDown, againstZealotButtonUp, againstZealotLabel);
		fillPanel(forChancePanel, forChanceTextField, forChanceButtonDown, forChanceButtonUp, forChanceLabel);
		fillPanel(againstChancePanel, againstChanceTextField, againstChanceButtonDown, againstChanceButtonUp, againstChanceLabel);

		manual.setMaximumSize(new Dimension(5, 30));

		speedTextField.setPreferredSize(new Dimension(50, 20));

		// filling fluent mode panel
		JPanel fluentMode = new JPanel();
		fluentMode.setLayout(new GridLayout(11, 1));
		fluentMode.setPreferredSize(new Dimension(260, 400));

		stop.setEnabled(false);

		mainButtonsPanel.setLayout(new GridLayout(1, 3));
		mainButtonsPanel.add(start);
		mainButtonsPanel.add(stop);
		mainButtonsPanel.add(reset);

		fluentSpeedButtonPanel.add(speedDown);
		fluentSpeedButtonPanel.add(speedTextField);
		fluentSpeedButtonPanel.add(speedUp);
		fluentMode.add(mainButtonsPanel);
		fluentMode.add(speedLabel1);
		fluentMode.add(fluentSpeedButtonPanel, BorderLayout.CENTER);

		// filling stepOverMode panel
		JPanel stepOverMode = new JPanel();
		stepOverMode.setLayout(new GridLayout(11, 1));
		stepOverMode.setPreferredSize(new Dimension(260, 400));

		stepOverStop.setEnabled(false);
		stepOverStart.setEnabled(false);

		mainStepOverButtonPanel.setLayout(new GridLayout(1, 3));
		mainStepOverButtonPanel.add(stepOverStart);
		mainStepOverButtonPanel.add(stepOverStop);
		mainStepOverButtonPanel.add(stepOverReset);

		stepOverSpeedTextField.setPreferredSize(new Dimension(50, 20));

		simulateButton.setEnabled(false);

		stepOverSpeedButtonPanel.add(stepOverSpeedDown);
		stepOverSpeedButtonPanel.add(stepOverSpeedTextField);
		stepOverSpeedButtonPanel.add(stepOverSpeedUp);
		stepOverButton.setEnabled(false);
		stepOverMode.add(mainStepOverButtonPanel);
		stepOverMode.add(speedLabel2);
		stepOverMode.add(stepOverSpeedButtonPanel, BorderLayout.CENTER);
		stepOverMode.add(simulateButton);
		stepOverMode.add(stepOverButton);

		// filling tabbedPane
		tabbedPane.setPreferredSize(new Dimension(260, 400));
		tabbedPane.addTab("Settings", leftPanel);
		tabbedPane.addTab("Fluent", fluentMode);
		tabbedPane.addTab("Step Over", stepOverMode);
		tabbedPane.setBackgroundAt(1, Color.green);
		agentsPane.setLayout(new BorderLayout());
		agentsPane.add(manual, BorderLayout.SOUTH);
		this.add(tabbedPane, BorderLayout.WEST);
		this.add(agentsPane, BorderLayout.EAST);
		this.add(chartPane, BorderLayout.SOUTH);
	}

	private void fillPanel(JPanel panel, TextField textField, Button buttonDown, Button buttonUp, JLabel label){
		textField.setPreferredSize(new Dimension(50, 20));
		panel.add(buttonDown);
		panel.add(textField);
		panel.add(buttonUp);
		leftPanel.add(label);
		leftPanel.add(panel, BorderLayout.CENTER);
	}

	private void initListeners() {

		manual.addActionListener(e -> JOptionPane.showMessageDialog(null, "Manual\n"
				+ "This program simulates opinion changes in group of agents. To simulate agents impact on their neighbors, majority model was implemented. \n"
				+ " It considers triads that form inside the group and overall opinion that is being set in each of them after agents connect with each other.\n"
				+ "This program allows inspecting agents group by setting the initial number of positive (for) opinions, number of Zealots (agents with\n"
				+ "static opinions) and their opinions, external opinion fields, that can influence agents on changing their opinions and chance of settling\n "
				+ " uniform opinion in a triad.\n" + "It is possible to run simulations in two modes:\n"
				+ "1)	Fluent simulation – runs fluent simulation. Every generation of agents and their opinions are visualized on panel at the ride side of\n"
				+ " the window and are represented by coloured circles. Blue color means that an agent has a positive opinion and red means negative opinion.\n"
				+ " At the bottom of the window is chart that illustrates overall opinions change of the group through time.\n"
				+ "2)	Step over – runs simulation in step-by-step mode in which it is possible to observe resultant opinion in each triad. Circles that represent\n "
				+ " current triad’s agents are connected on the graph with lines.\n"
				+ "To run simulation in one of the modes users must go to one of the Fluent or Step Over tabs and then click reset button to make program handle one\n"
				+ " of modes. At the end click Start button and see simulation progresses. To stop simulation execution click Stop button. Exclusively for Step Over\n"
				+ " mode there is also a Simulation/Step Over button that allows speeding up execution in step-by-step mode.\n"
				+ "To change simulation speed user can type in a new value in the Speed Text Field or change it using buttons Up or Down at the sides of a field.\n"
				+ "In Settings tab can be found all necessary parameters mentioned above (except simulation speed that can be changed in modes own tabs).\n"
				+ "If background of one of the parameters text fields glows yellow, it means its value does not match parameter’s value that program is using. To\n"
				+ " make value update it is necessary to restart program than it will update values on its own.\n"
				+ "If background of one of the parameters text fields glows red, it means value in field in set badly (in terms of a value or format) and must\n"
				+ " change to run program.\n" + "", "Manual", JOptionPane.PLAIN_MESSAGE));

		start.addActionListener(e -> {
			tabbedPane.setEnabledAt(2, false);
			tabbedPane.setBackgroundAt(2, Color.red);
			if (!pop.getIsRunning()) {
				pop.setIsRunning(true);
				stop.setEnabled(true);
				start.setEnabled(false);
			}
		});

		stop.addActionListener(e -> {
			if (pop.getIsRunning()) {
				pop.setIsRunning(false);
				stop.setEnabled(false);
				start.setEnabled(true);
			}
			tabbedPane.setEnabledAt(2, true);
			tabbedPane.setBackgroundAt(2, getBackground());
		});

		reset.addActionListener(e -> {
			pop.setIsRunning(false);
			updateParameters();
			setMode(Mode.FLUENT);
			cleanQueues();
			pop.setParameters(numberOfAgents, initialFor, forField, againstField, forChance, againstChance,
					forZealots, againstZealots, Integer.parseInt(speedTextField.getText()));
			agentsPane.setParameters(numberOfAgents, initialFor);
			agentsPane.update();
			chartPane.setParameters(initialFor, forField, againstField, forChance, againstChance, forZealots,
					againstZealots);
			chartPane.restart();
			giveQueuesSomeFood();
			stepOverStop.setEnabled(false);
			stepOverStart.setEnabled(false);
			tabbedPane.setEnabledAt(2, true);
			stop.setEnabled(false);
			start.setEnabled(true);
			colorAllFields();
			tabbedPane.setBackgroundAt(1, Color.green);
			tabbedPane.setBackgroundAt(2, getBackground());
		});

		agentUp.incrementInteger();
		agentDown.decrementInteger();
		populationCountTextField.addInputCheckerInteger();

		speedUp.incrementSpeed();
		speedDown.decrementSpeed();
		speedTextField.addInputCheckerSpeed();

		initForDown.incrementDouble();
		initForUp.decrementDouble();
		initForTextField.addInputCheckerDouble();

		forFieldButtonUp.incrementDouble();
		forFieldButtonDown.decrementDouble();
		forFieldTextField.addInputCheckerDouble();

		againstFieldButtonUp.incrementDouble();
		againstFieldButtonDown.decrementDouble();
		againstFieldTextField.addInputCheckerDouble();

		forZealotButtonUp.incrementInteger();
		forZealotButtonDown.decrementInteger();
		forZealotTextField.addInputCheckerInteger();

		againstZealotButtonUp.incrementInteger();
		againstZealotButtonDown.decrementInteger();
		againstZealotTextField.addInputCheckerInteger();

		forChanceButtonUp.incrementDouble();
		forChanceButtonDown.decrementDouble();
		forChanceTextField.addInputCheckerDouble();

		againstChanceButtonUp.incrementDouble();
		againstChanceButtonDown.decrementDouble();
		againstChanceTextField.addInputCheckerDouble();

		stepOverStart.addActionListener(e -> {
			tabbedPane.setEnabledAt(1, false);
			tabbedPane.setBackgroundAt(1, Color.red);
			if (!pop.getIsRunning()) {
				pop.setIsRunning(true);
				agentsPane.setSpeed(Integer.parseInt(stepOverSpeedTextField.getText()));
				agentsPane.setSimulationStop(false);
				if(agentsPane.getMode() == Mode.STEPOVER && agentsPane.getSimulation()) {
					try {
						nextStep.put(true);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				stop.setEnabled(true);
				start.setEnabled(false);
				simulateButton.setEnabled(true);
				if(!agentsPane.getSimulation())
					stepOverButton.setEnabled(true);
				stepOverStop.setEnabled(true);
				stepOverStart.setEnabled(false);
			}
		});

		stepOverStop.addActionListener(e -> {
			if (pop.getIsRunning()) {
				pop.setIsRunning(false);
				stop.setEnabled(false);
				agentsPane.setSimulationStop(true);
				simulateButton.setEnabled(false);
				stepOverButton.setEnabled(false);
				stepOverStop.setEnabled(false);
				stepOverStart.setEnabled(true);
			}
			tabbedPane.setEnabledAt(1, true);
			tabbedPane.setBackgroundAt(1, getBackground());
		});

		stepOverReset.addActionListener(e -> {
			pop.setIsRunning(false);
			updateParameters();
			if(agentsPane.getMode() == Mode.STEPOVER) {
				agentsPane.setNewSimulation(true);
				try {
					nextStep.put(true);
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
			}
			cleanQueues();
			setMode(Mode.STEPOVER);
			pop.setParameters(numberOfAgents, initialFor, forField, againstField, forChance, againstChance,
					forZealots, againstZealots, Integer.parseInt(stepOverSpeedTextField.getText()));
			agentsPane.setParameters(numberOfAgents, initialFor);
			agentsPane.setSpeed(Integer.parseInt(stepOverSpeedTextField.getText()));
			agentsPane.update();
			chartPane.setParameters(initialFor, forField, againstField, forChance, againstChance, forZealots,
					againstZealots);
			chartPane.restart();
			stop.setEnabled(false);
			start.setEnabled(false);
			tabbedPane.setEnabledAt(1, true);
			stepOverStop.setEnabled(false);
			stepOverStart.setEnabled(true);
			stepOverButton.setEnabled(false);
			colorAllFields();
			tabbedPane.setBackgroundAt(2, Color.green);
			tabbedPane.setBackgroundAt(1, getBackground());
		});

		stepOverSpeedUp.incrementInteger();
		stepOverSpeedDown.decrementInteger();
		simulateButton.addActionListener(e -> {
			if (agentsPane.getMode() == Mode.STEPOVER && stepOverButton.isEnabled()) {
				agentsPane.setSimulation(true);
				stepOverButton.setEnabled(false);
				simulateButton.setText("Step Over");
				if (nextStep.isEmpty()) {
					try {
						nextStep.put(true);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				try {
					if(nextStep.isEmpty()) {
						nextStep.put(true);
					}
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
				agentsPane.setSimulation(false);
				stepOverButton.setEnabled(true);
				simulateButton.setText("Simulate");
			}
		});

		stepOverSpeedTextField.addInputCheckerSpeed();

		stepOverButton.addActionListener(e -> {
			try {
				nextStep.put(true);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		});
	}

	private void colorAllFields() {
		textFields.forEach(t -> t.setBackground(Color.WHITE));
	}

	private void setMode(Mode mode) {
		pop.setMode(mode);
		agentsPane.setMode(mode);
		chartPane.setMode(mode);
	}

	private void cleanQueues() {
		queuesList.forEach(Collection::clear);
	}

	private void giveQueuesSomeFood() {
		try {
			controlSimFlow.put(true);
			nextStep.put(true);
			updateChart.put(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void updateParameters(){
		numberOfAgents = Integer.parseInt(populationCountTextField.getText());
		forZealots = Integer.parseInt(forZealotTextField.getText());
		againstZealots = Integer.parseInt(againstZealotTextField.getText());
		initialFor = Double.parseDouble(initForTextField.getText());
		forField = Double.parseDouble(forFieldTextField.getText());
		againstField = Double.parseDouble(againstFieldTextField.getText());
		forChance = Double.parseDouble(forChanceTextField.getText());
		againstChance = Double.parseDouble(againstChanceTextField.getText());
	}
}