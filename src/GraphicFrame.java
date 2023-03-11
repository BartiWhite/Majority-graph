import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GraphicFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static PopulationController pop;
	private static AgentsPane agentsPane;
	private static ChartPane chartPane;
	private final JTabbedPane tabbedPane;
	private BlockingQueue<List<Opinion>> graphQueue = new ArrayBlockingQueue<>(5),
			chartQueue = new ArrayBlockingQueue<>(5);
	private BlockingQueue<List<Integer>> opinionIndexesQueue = new ArrayBlockingQueue<>(5);
	private BlockingQueue<Boolean> nextStep = new ArrayBlockingQueue<>(1), controlSimFlow = new ArrayBlockingQueue<>(1),
			updateChart = new ArrayBlockingQueue<>(1);
	private final JButton start, stop, reset, stepOverStart, stepOverStop, stepOverReset, agentUp, agentDown, speedUp,
			speedDown, stepOverSpeedUp, stepOverSpeedDown, simulateButton, initForDown, initForUp, forFieldButtonDown,
			forFieldButtonUp, againstFieldButtonDown, againstFieldButtonUp, stepOverButton, forZealotButtonUp,
			againstZealotButtonUp, forZealotButtonDown, againstZealotButtonDown, forChanceButtonUp,
			againstChanceButtonUp, forChanceButtonDown, againstChanceButtonDown, manual;
	private final JTextField populationCountTextField, speedTextField, stepOverSpeedTextField, initForTextField,
			forFieldTextField, againstFieldTextField, forZealotTextField, againstZealotTextField, forChanceTextField,
			againstChanceTextField;
	private final JPanel leftPanel, mainButtonsPanel, mainStepOverButtonPanel, populationCountButtonPanel,
			fluentSpeedButtonPanel, stepOverSpeedButtonPanel, initForPanel, forFieldPanel, againstFieldPanel,
			forZealotPanel, againstZealotPanel, forChancePanel, againstChancePanel;
	private final JLabel speedLabel1, speedLabel2, populationCountLabel, initForLabel, forFieldLabel, againstFieldLabel,
			forZealotLabel, againstZealotLabel, forChanceLabel, againstChanceLabel;
	private int numberOfAgents = 20, forZealots = 0, againstZealots = 0;
	private double initialFor = 0.5, forField = 0, againstField = 0, forChance = 1, againstChance = 1;

	public GraphicFrame() {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("MajorityGraph");
		this.setSize(900, 760);
		this.setMinimumSize(new Dimension(900, 760));
//		this.setResizable(false);
		this.setLayout(new BorderLayout());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				pop.setIsRunning(false);
				System.exit(0);
			}
		});

		pop = new PopulationController(graphQueue, opinionIndexesQueue, chartQueue, controlSimFlow);
		agentsPane = new AgentsPane(graphQueue, opinionIndexesQueue, nextStep, controlSimFlow, updateChart);
		chartPane = new ChartPane(chartQueue, updateChart);

		manual = new JButton("Manual");
		leftPanel = new JPanel();
		start = new JButton("Start");
		stop = new JButton("Stop");
		reset = new JButton("Reset");
		stepOverStart = new JButton("Start");
		stepOverStop = new JButton("Stop");
		stepOverReset = new JButton("Reset");
		simulateButton = new JButton("Simulate");
		mainButtonsPanel = new JPanel();
		mainStepOverButtonPanel = new JPanel();
		agentUp = new JButton("Up");
		agentDown = new JButton("Down");
		populationCountTextField = new JTextField("20");
		populationCountButtonPanel = new JPanel();
		populationCountLabel = new JLabel("Manage nr. of agents", SwingConstants.CENTER);
		speedUp = new JButton("Up");
		speedDown = new JButton("Down");
		stepOverSpeedUp = new JButton("Up");
		stepOverSpeedDown = new JButton("Down");
		speedTextField = new JTextField("1");
		stepOverSpeedTextField = new JTextField("1");
		fluentSpeedButtonPanel = new JPanel();
		stepOverSpeedButtonPanel = new JPanel();
		speedLabel1 = new JLabel("Set speed of symulation", SwingConstants.CENTER);
		speedLabel2 = new JLabel("Set speed of symulation", SwingConstants.CENTER);
		initForDown = new JButton("Down");
		initForUp = new JButton("Up");
		initForTextField = new JTextField("0.5");
		initForPanel = new JPanel();
		initForLabel = new JLabel("Set initial nr. of for opinions", SwingConstants.CENTER);
		forFieldButtonDown = new JButton("Down");
		forFieldButtonUp = new JButton("Up");
		forFieldTextField = new JTextField("0");
		forFieldPanel = new JPanel();
		forFieldLabel = new JLabel("Set for field", SwingConstants.CENTER);
		againstFieldButtonDown = new JButton("Down");
		againstFieldButtonUp = new JButton("Up");
		againstFieldTextField = new JTextField("0");
		againstFieldPanel = new JPanel();
		againstFieldLabel = new JLabel("Set against field", SwingConstants.CENTER);
		stepOverButton = new JButton("Next step");
		tabbedPane = new JTabbedPane();

		forZealotButtonUp = new JButton("Up");
		againstZealotButtonUp = new JButton("Up");
		forZealotButtonDown = new JButton("Down");
		againstZealotButtonDown = new JButton("Down");
		forChanceButtonUp = new JButton("Up");
		againstChanceButtonUp = new JButton("Up");
		forChanceButtonDown = new JButton("Down");
		againstChanceButtonDown = new JButton("Down");
		forZealotTextField = new JTextField("0");
		againstZealotTextField = new JTextField("0");
		forChanceTextField = new JTextField("1");
		againstChanceTextField = new JTextField("1");
		forZealotPanel = new JPanel();
		againstZealotPanel = new JPanel();
		forChancePanel = new JPanel();
		againstChancePanel = new JPanel();
		forZealotLabel = new JLabel("Set for zealots number", SwingConstants.CENTER);
		againstZealotLabel = new JLabel("Set against zealots number", SwingConstants.CENTER);
		forChanceLabel = new JLabel("Set for chance", SwingConstants.CENTER);
		againstChanceLabel = new JLabel("Set against chance", SwingConstants.CENTER);

		initGUI();
		initListeners();

		Thread graphThread = new Thread(agentsPane);
		Thread charThread = new Thread(chartPane);
		Thread controllerThread = new Thread(pop);
		graphThread.start();
		charThread.start();
		controllerThread.start();
	}

	private final void initGUI() {
		leftPanel.setLayout(new GridLayout(16, 1));

		manual.setMaximumSize(new Dimension(5, 30));

		speedTextField.setPreferredSize(new Dimension(50, 20));
		populationCountTextField.setPreferredSize(new Dimension(50, 20));

		populationCountButtonPanel.setLayout(new FlowLayout());
		populationCountButtonPanel.add(agentDown);
		populationCountButtonPanel.add(populationCountTextField);
		populationCountButtonPanel.add(agentUp);
		leftPanel.add(populationCountLabel);
		leftPanel.add(populationCountButtonPanel);

		initForTextField.setPreferredSize(new Dimension(50, 20));

		initForPanel.add(initForDown);
		initForPanel.add(initForTextField);
		initForPanel.add(initForUp);
		leftPanel.add(initForLabel);
		leftPanel.add(initForPanel, BorderLayout.CENTER);

		forFieldTextField.setPreferredSize(new Dimension(50, 20));

		forFieldPanel.setLayout(new FlowLayout());
		forFieldPanel.add(forFieldButtonDown);
		forFieldPanel.add(forFieldTextField);
		forFieldPanel.add(forFieldButtonUp);
		leftPanel.add(forFieldLabel);
		leftPanel.add(forFieldPanel, BorderLayout.CENTER);

		againstFieldTextField.setPreferredSize(new Dimension(50, 20));

		againstFieldPanel.setLayout(new FlowLayout());
		againstFieldPanel.add(againstFieldButtonDown);
		againstFieldPanel.add(againstFieldTextField);
		againstFieldPanel.add(againstFieldButtonUp);
		leftPanel.add(againstFieldLabel);
		leftPanel.add(againstFieldPanel, BorderLayout.CENTER);

		forZealotTextField.setPreferredSize(new Dimension(50, 20));

		forZealotPanel.setLayout(new FlowLayout());
		forZealotPanel.add(forZealotButtonDown);
		forZealotPanel.add(forZealotTextField);
		forZealotPanel.add(forZealotButtonUp);
		leftPanel.add(forZealotLabel);
		leftPanel.add(forZealotPanel, BorderLayout.CENTER);

		againstZealotTextField.setPreferredSize(new Dimension(50, 20));

		againstZealotPanel.setLayout(new FlowLayout());
		againstZealotPanel.add(againstZealotButtonDown);
		againstZealotPanel.add(againstZealotTextField);
		againstZealotPanel.add(againstZealotButtonUp);
		leftPanel.add(againstZealotLabel);
		leftPanel.add(againstZealotPanel, BorderLayout.CENTER);

		forChanceTextField.setPreferredSize(new Dimension(50, 20));

		forChancePanel.setLayout(new FlowLayout());
		forChancePanel.add(forChanceButtonDown);
		forChancePanel.add(forChanceTextField);
		forChancePanel.add(forChanceButtonUp);
		leftPanel.add(forChanceLabel);
		leftPanel.add(forChancePanel, BorderLayout.CENTER);

		againstChanceTextField.setPreferredSize(new Dimension(50, 20));

		againstChancePanel.setLayout(new FlowLayout());
		againstChancePanel.add(againstChanceButtonDown);
		againstChancePanel.add(againstChanceTextField);
		againstChancePanel.add(againstChanceButtonUp);
		leftPanel.add(againstChanceLabel);
		leftPanel.add(againstChancePanel, BorderLayout.CENTER);

		JPanel fluentMode = new JPanel();
		fluentMode.setLayout(new GridLayout(11, 1));
		fluentMode.setPreferredSize(new Dimension(260, 400));

		stop.setEnabled(false);

		mainButtonsPanel.setLayout(new GridLayout(1, 3));
		mainButtonsPanel.add(start);
		mainButtonsPanel.add(stop);
		mainButtonsPanel.add(reset);

		fluentSpeedButtonPanel.setLayout(new FlowLayout());
		fluentSpeedButtonPanel.add(speedDown);
		fluentSpeedButtonPanel.add(speedTextField);
		fluentSpeedButtonPanel.add(speedUp);
		fluentMode.add(mainButtonsPanel);
		fluentMode.add(speedLabel1);
		fluentMode.add(fluentSpeedButtonPanel, BorderLayout.CENTER);

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

		stepOverSpeedButtonPanel.setLayout(new FlowLayout());
		stepOverSpeedButtonPanel.add(stepOverSpeedDown);
		stepOverSpeedButtonPanel.add(stepOverSpeedTextField);
		stepOverSpeedButtonPanel.add(stepOverSpeedUp);
		stepOverButton.setEnabled(false);
		stepOverMode.add(mainStepOverButtonPanel);
		stepOverMode.add(speedLabel2);
		stepOverMode.add(stepOverSpeedButtonPanel, BorderLayout.CENTER);
		stepOverMode.add(simulateButton);
		stepOverMode.add(stepOverButton);

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

	private final void initListeners() {

		manual.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Manual\n"
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
						+ " change to run program.\n" + "", "Manual", JOptionPane.DEFAULT_OPTION);
			}
		});

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setEnabledAt(2, false);
				tabbedPane.setBackgroundAt(2, Color.red);
				if (!pop.getIsRunning()) {
					pop.setIsRunning(true);
					stop.setEnabled(true);
					start.setEnabled(false);
				}
			}
		});

		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (pop.getIsRunning()) {
					pop.setIsRunning(false);
					stop.setEnabled(false);
					start.setEnabled(true);
				}
				tabbedPane.setEnabledAt(2, true);
				tabbedPane.setBackgroundAt(2, getBackground());
			}
		});

		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pop.setIsRunning(false);
				setMode(Mode.FLUENT);
				cleanQueues();
				pop.setParameters(numberOfAgents, initialFor, forField, againstField,
						Integer.valueOf(speedTextField.getText()));
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
			}
		});

		agentUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementNumberOfAgents();
				populationCountTextField.setText(String.valueOf(getNumberOfAgents()));
				colorPopulationCountField();
			}
		});

		agentDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				decrementNumberOfAgents();
				populationCountTextField.setText(String.valueOf(getNumberOfAgents()));
				colorPopulationCountField();
			}
		});

		populationCountTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryInsert();
			}

			private void tryInsert() {
				try {
					int value = Integer.parseInt(populationCountTextField.getText());
					if (2 < value && value < 1001) {
						numberOfAgents = value;
						setTrue();
					} else
						setFalse();

					colorPopulationCountField();
				} catch (NumberFormatException e1) {
					populationCountTextField.setBackground(Color.red);
					setFalse();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void setTrue() {
				enableButtons(true);
				agentDown.setEnabled(true);
				agentUp.setEnabled(true);
			}

			private void setFalse() {
				enableButtons(false);
				agentDown.setEnabled(false);
				agentUp.setEnabled(false);
			}

		});
		populationCountTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					if (2 < Integer.parseInt(populationCountTextField.getText())
							&& Integer.parseInt(populationCountTextField.getText()) < 1001)
						numberOfAgents = Integer.parseInt(populationCountTextField.getText());
					else
						populationCountTextField.setText(String.valueOf(numberOfAgents));
				} catch (NumberFormatException e2) {
				}
			}
		});

		speedUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (pop.getSpeed() < 100) {
					pop.incrementSpeed();
					speedTextField.setText(String.valueOf(pop.getSpeed()));
				}
			}
		});

		speedDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (pop.getSpeed() > 1) {
					pop.decrementSpeed();
					speedTextField.setText(String.valueOf(pop.getSpeed()));
				}
			}
		});

		speedTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void tryInsert() {
				try {
					int value = Integer.parseInt(speedTextField.getText());
					if (0 < value && value < 101)
						setTrue();
					else
						setFalse();

					colorSpeedField();
				} catch (NumberFormatException e1) {
					speedTextField.setBackground(Color.red);
					setFalse();
				}
			}

			private void setTrue() {
				enableButtons(true);
				speedDown.setEnabled(true);
				speedUp.setEnabled(true);
			}

			private void setFalse() {
				enableButtons(false);
				speedDown.setEnabled(false);
				speedUp.setEnabled(false);
			}
		});

		speedTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					if (0 < Integer.parseInt(speedTextField.getText())
							&& Integer.parseInt(speedTextField.getText()) < 101)
						pop.setSpeed(Integer.parseInt(speedTextField.getText()));
					else
						speedTextField.setText(String.valueOf(pop.getSpeed()));
					colorSpeedField();
				} catch (NumberFormatException e2) {
				}
			}
		});

		initForDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				decrementInitialFor();
				initForTextField.setText(
						String.valueOf(BigDecimal.valueOf(initialFor).setScale(2, RoundingMode.HALF_UP).doubleValue()));
				colorInitialForField();
			}
		});

		initForUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementInitialFor();
				initForTextField.setText(
						String.valueOf(BigDecimal.valueOf(initialFor).setScale(2, RoundingMode.HALF_UP).doubleValue()));
				colorInitialForField();
			}
		});

		initForTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryInsert();
			}

			private void tryInsert() {
				try {
					double value = Double.parseDouble(initForTextField.getText());
					if (0 <= value && value <= 1) {
						initialFor = value;
						setTrue();
					} else
						setFalse();

					colorInitialForField();
				} catch (NumberFormatException e1) {
					initForTextField.setBackground(Color.red);
					setFalse();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void setTrue() {
				enableButtons(true);
				initForDown.setEnabled(true);
				initForUp.setEnabled(true);
			}

			private void setFalse() {
				enableButtons(false);
				initForDown.setEnabled(false);
				initForUp.setEnabled(false);
			}
		});

		initForTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (0 <= Double.parseDouble(initForTextField.getText())
							&& Double.parseDouble(initForTextField.getText()) <= 1)
						initialFor = Double.parseDouble(initForTextField.getText());
					else
						initForTextField.setText(String.valueOf(initialFor));
				} catch (NumberFormatException e2) {
				}
			}

		});

		forFieldButtonDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				decrementForField();
				forFieldTextField.setText(
						String.valueOf(BigDecimal.valueOf(forField).setScale(2, RoundingMode.HALF_UP).doubleValue()));
				colorForFieldField();
			}
		});

		forFieldButtonUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementForField();
				forFieldTextField.setText(
						String.valueOf(BigDecimal.valueOf(forField).setScale(2, RoundingMode.HALF_UP).doubleValue()));
				colorForFieldField();
			}
		});

		forFieldTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void tryInsert() {
				try {
					double value = Double.parseDouble(forFieldTextField.getText());
					if (0 <= value && value <= 1) {
						forField = value;
						setTrue();
					} else
						setFalse();

					colorForFieldField();
				} catch (NumberFormatException e1) {
					forFieldTextField.setBackground(Color.red);
					setFalse();
				}
			}

			private void setTrue() {
				enableButtons(true);
				forFieldButtonDown.setEnabled(true);
				forFieldButtonUp.setEnabled(true);
			}

			private void setFalse() {
				enableButtons(false);
				forFieldButtonDown.setEnabled(false);
				forFieldButtonUp.setEnabled(false);
			}
		});

		forFieldTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (0 <= Double.parseDouble(forFieldTextField.getText())
							&& Double.parseDouble(forFieldTextField.getText()) <= 1)
						forField = Double.parseDouble(forFieldTextField.getText());
					else
						forFieldTextField.setText(String.valueOf(forField));
				} catch (NumberFormatException e2) {
				}
			}
		});

		againstFieldButtonDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				decrementAgainstField();
				againstFieldTextField.setText(String
						.valueOf(BigDecimal.valueOf(againstField).setScale(2, RoundingMode.HALF_UP).doubleValue()));
				colorAgainstFieldField();
			}
		});

		againstFieldButtonUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementAgainstField();
				againstFieldTextField.setText(String
						.valueOf(BigDecimal.valueOf(againstField).setScale(2, RoundingMode.HALF_UP).doubleValue()));
				colorAgainstFieldField();
			}
		});

		againstFieldTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void tryInsert() {
				try {
					double value = Double.parseDouble(againstFieldTextField.getText());
					if (0 <= value && value <= 1) {
						againstField = value;
						setTrue();
					} else
						setFalse();

					colorAgainstFieldField();
				} catch (NumberFormatException e1) {
					againstFieldTextField.setBackground(Color.red);
					setFalse();
				}
			}

			private void setTrue() {
				enableButtons(true);
				againstFieldButtonDown.setEnabled(true);
				againstFieldButtonUp.setEnabled(true);
			}

			private void setFalse() {
				enableButtons(false);
				againstFieldButtonDown.setEnabled(false);
				againstFieldButtonUp.setEnabled(false);
			}

		});

		againstFieldTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (0 <= Double.parseDouble(againstFieldTextField.getText())
							&& Double.parseDouble(againstFieldTextField.getText()) <= 1)
						againstField = Double.parseDouble(againstFieldTextField.getText());
					else
						againstFieldTextField.setText(String.valueOf(againstField));
				} catch (NumberFormatException e2) {
				}
			}
		});

		stepOverStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setEnabledAt(1, false);
				tabbedPane.setBackgroundAt(1, Color.red);
				if (!pop.getIsRunning()) {
					pop.setIsRunning(true);
					agentsPane.setSpeed(Integer.parseInt(stepOverSpeedTextField.getText()));
					agentsPane.setSimulationStop(false);
					if(agentsPane.getMode() == Mode.STEPOVER && agentsPane.getSimulation())
					{
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
			}
		});

		stepOverStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
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
			}
		});

		stepOverReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pop.setIsRunning(false);
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
				pop.setParameters(numberOfAgents, initialFor, forField, againstField,
						Integer.valueOf(stepOverSpeedTextField.getText()));
				agentsPane.setParameters(numberOfAgents, initialFor);
				agentsPane.setSpeed(Integer.valueOf(stepOverSpeedTextField.getText()));
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
			}
		});

		stepOverSpeedUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (agentsPane.getSpeed() < 100) {
					agentsPane.incrementSpeed();
					stepOverSpeedTextField.setText(String.valueOf(agentsPane.getSpeed()));
				}
			}
		});

		stepOverSpeedDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (agentsPane.getSpeed() > 1) {
					agentsPane.decrementSpeed();
					stepOverSpeedTextField.setText(String.valueOf(agentsPane.getSpeed()));
				}
			}
		});

		simulateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (agentsPane.getMode() == Mode.STEPOVER && stepOverButton.isEnabled()) {
					agentsPane.setSimulation(true);
					stepOverButton.setEnabled(false);
					simulateButton.setText("Step Over");
					if (nextStep.isEmpty())
						try {
							nextStep.put(true);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
				} else {
					try {
						if(nextStep.isEmpty())
							nextStep.put(true);
					} catch (InterruptedException ex) {
						throw new RuntimeException(ex);
					}
					agentsPane.setSimulation(false);
					stepOverButton.setEnabled(true);
					simulateButton.setText("Simulate");
				}
			}
		});

		stepOverSpeedTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void tryInsert() {
				try {
					int value = Integer.parseInt(stepOverSpeedTextField.getText());
					if (0 < value && value < 101)
						setTrue();
					else
						setFalse();

					colorStepOverSpeedField();
				} catch (NumberFormatException e1) {
					stepOverSpeedTextField.setBackground(Color.red);
					setFalse();
				}
			}

			private void setTrue() {
				enableButtons(true);
				speedDown.setEnabled(true);
				speedUp.setEnabled(true);
			}

			private void setFalse() {
				enableButtons(false);
				speedDown.setEnabled(false);
				speedUp.setEnabled(false);
			}
		});

		stepOverSpeedTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					if (0 < Integer.parseInt(stepOverSpeedTextField.getText())
							&& Integer.parseInt(stepOverSpeedTextField.getText()) < 101)
						agentsPane.setSpeed(Integer.parseInt(stepOverSpeedTextField.getText()));
					else
						stepOverSpeedTextField.setText(String.valueOf(agentsPane.getSpeed()));
					colorStepOverSpeedField();
				} catch (NumberFormatException e2) {
				}
			}
		});

		stepOverButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					nextStep.put(true);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});

		forZealotButtonDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				decrementForZealots();
				forZealotTextField.setText(String.valueOf(forZealots));
				colorForZealotTextField();
			}
		});

		forZealotButtonUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementForZealots();
				forZealotTextField.setText(String.valueOf(forZealots));
				colorForZealotTextField();
			}
		});

		forZealotTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void tryInsert() {
				try {
					int value = Integer.parseInt(forZealotTextField.getText());
					if (-1 < value && value < numberOfAgents)
						setTrue();
					else
						setFalse();

					colorForZealotTextField();
				} catch (NumberFormatException e1) {
					forZealotTextField.setBackground(Color.red);
					setFalse();
				}
			}

			private void setTrue() {
				enableButtons(true);
				forZealotButtonDown.setEnabled(true);
				forZealotButtonUp.setEnabled(true);
			}

			private void setFalse() {
				enableButtons(false);
				forZealotButtonDown.setEnabled(false);
				forZealotButtonUp.setEnabled(false);
			}

		});

		forZealotTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (-1 < Integer.parseInt(forZealotTextField.getText())
							&& Integer.parseInt(forZealotTextField.getText()) < 1001)
						forZealots = Integer.parseInt(forZealotTextField.getText());
					else
						forZealotTextField.setText(String.valueOf(forZealots));
				} catch (NumberFormatException e2) {
				}
			}
		});

		againstZealotButtonDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				decrementAgainstZealots();
				againstZealotTextField.setText(String.valueOf(againstZealots));
				colorAgainstZealotTextField();
			}
		});
//
		againstZealotButtonUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementAgainstZealots();
				againstZealotTextField.setText(String.valueOf(againstZealots));
				colorAgainstZealotTextField();
			}
		});

		againstZealotTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void tryInsert() {
				try {
					int value = Integer.parseInt(againstZealotTextField.getText());
					if (-1 < value && value < numberOfAgents)
						setTrue();
					else
						setFalse();

					colorAgainstZealotTextField();
				} catch (NumberFormatException e1) {
					againstZealotTextField.setBackground(Color.red);
					setFalse();
				}
			}

			private void setTrue() {
				enableButtons(true);
				againstZealotButtonDown.setEnabled(true);
				againstZealotButtonUp.setEnabled(true);
			}

			private void setFalse() {
				enableButtons(false);
				againstZealotButtonDown.setEnabled(false);
				againstZealotButtonUp.setEnabled(false);
			}

		});

		againstZealotTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (-1 < Integer.parseInt(againstZealotTextField.getText())
							&& Integer.parseInt(againstZealotTextField.getText()) < 1001)
						againstZealots = Integer.parseInt(againstZealotTextField.getText());
					else
						againstZealotTextField.setText(String.valueOf(againstZealots));
				} catch (NumberFormatException e2) {
				}
			}
		});

		forChanceButtonDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				decrementForChance();
				forChanceTextField.setText(
						String.valueOf(BigDecimal.valueOf(forChance).setScale(2, RoundingMode.HALF_UP).doubleValue()));
				colorForChanceTextField();
			}
		});
//
		forChanceButtonUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementForChance();
				forChanceTextField.setText(
						String.valueOf(BigDecimal.valueOf(forChance).setScale(2, RoundingMode.HALF_UP).doubleValue()));
				colorForChanceTextField();
			}
		});

		forChanceTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void tryInsert() {
				try {
					double value = Double.parseDouble(forChanceTextField.getText());
					if (0 <= value && value <= 1) {
						forChance = value;
						setTrue();
					} else
						setFalse();

					colorForChanceTextField();
				} catch (NumberFormatException e1) {
					forChanceTextField.setBackground(Color.red);
					setFalse();
				}
			}

			private void setTrue() {
				enableButtons(true);
				forChanceButtonUp.setEnabled(true);
				forChanceButtonDown.setEnabled(true);
			}

			private void setFalse() {
				enableButtons(false);
				forChanceButtonUp.setEnabled(false);
				forChanceButtonDown.setEnabled(false);
			}
		});

		forChanceTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (0 <= Double.parseDouble(forChanceTextField.getText())
							&& Double.parseDouble(forChanceTextField.getText()) <= 1)
						forChance = Double.parseDouble(forChanceTextField.getText());
					else
						forChanceTextField.setText(String.valueOf(forChance));
				} catch (NumberFormatException e2) {
				}
			}
		});

		againstChanceButtonDown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				decrementAgainstChance();
				againstChanceTextField.setText(String
						.valueOf(BigDecimal.valueOf(againstChance).setScale(2, RoundingMode.HALF_UP).doubleValue()));
				colorAgainstChanceTextField();
			}
		});
//
		againstChanceButtonUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementAgainstChance();
				againstChanceTextField.setText(String
						.valueOf(BigDecimal.valueOf(againstChance).setScale(2, RoundingMode.HALF_UP).doubleValue()));
				colorAgainstChanceTextField();
			}
		});

		againstChanceTextField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				tryInsert();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}

			private void tryInsert() {
				try {
					double value = Double.parseDouble(againstChanceTextField.getText());
					if (0 <= value && value <= 1) {
						againstChance = value;
						setTrue();
					} else
						setFalse();

					colorAgainstChanceTextField();
				} catch (NumberFormatException e1) {
					againstChanceTextField.setBackground(Color.red);
					setFalse();
				}
			}

			private void setTrue() {
				enableButtons(true);
				againstChanceButtonUp.setEnabled(true);
				againstChanceButtonDown.setEnabled(true);
			}

			private void setFalse() {
				enableButtons(false);
				againstChanceButtonUp.setEnabled(false);
				againstChanceButtonDown.setEnabled(false);
			}
		});

		againstChanceTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (0 <= Double.parseDouble(againstChanceTextField.getText())
							&& Double.parseDouble(againstChanceTextField.getText()) <= 1)
						againstChance = Double.parseDouble(againstChanceTextField.getText());
					else
						againstChanceTextField.setText(String.valueOf(againstChance));
				} catch (NumberFormatException e2) {
				}
			}
		});
	}

	void enableButtons(boolean isEnabled) {
		if (isEnabled) {
			reset.setEnabled(true);
			start.setEnabled(true);
			stop.setEnabled(true);
			stepOverReset.setEnabled(true);
			stepOverStart.setEnabled(true);
			stepOverStop.setEnabled(true);
		} else {
			reset.setEnabled(false);
			start.setEnabled(false);
			stop.setEnabled(false);
			stepOverReset.setEnabled(false);
			stepOverStart.setEnabled(false);
			stepOverStop.setEnabled(false);
		}
	}

	private void colorPopulationCountField() {
		if (Integer.parseInt(populationCountTextField.getText()) > 1000)
			populationCountTextField.setBackground(Color.red);
		else if (Integer.parseInt(populationCountTextField.getText()) < 3)
			populationCountTextField.setBackground(Color.red);
		else if (numberOfAgents != agentsPane.getNumberOfAgents())
			populationCountTextField.setBackground(Color.yellow);
		else
			populationCountTextField.setBackground(Color.white);
	}

	private void colorSpeedField() {
		if (Integer.parseInt(speedTextField.getText()) > 100)
			speedTextField.setBackground(Color.red);
		else if (Integer.parseInt(speedTextField.getText()) < 1)
			speedTextField.setBackground(Color.red);
		else if (pop.getSpeed() != Integer.parseInt(speedTextField.getText()))
			speedTextField.setBackground(Color.yellow);
		else
			speedTextField.setBackground(Color.white);
	}

	private void colorStepOverSpeedField() {
		if (Integer.parseInt(stepOverSpeedTextField.getText()) > 100)
			stepOverSpeedTextField.setBackground(Color.red);
		else if (Integer.parseInt(stepOverSpeedTextField.getText()) < 1)
			stepOverSpeedTextField.setBackground(Color.red);
		else if (agentsPane.getSpeed() != Integer.parseInt(stepOverSpeedTextField.getText()))
			stepOverSpeedTextField.setBackground(Color.yellow);
		else
			stepOverSpeedTextField.setBackground(Color.white);
	}

	private void colorInitialForField() {
		if (Double.parseDouble(initForTextField.getText()) > 1)
			initForTextField.setBackground(Color.red);
		else if (Double.parseDouble(initForTextField.getText()) < 0)
			initForTextField.setBackground(Color.red);
		else if (initialFor != pop.getInitialFor())
			initForTextField.setBackground(Color.yellow);
		else
			initForTextField.setBackground(Color.white);
	}

	private void colorForFieldField() {
		if (Double.parseDouble(forFieldTextField.getText()) > 1)
			forFieldTextField.setBackground(Color.red);
		else if (Double.parseDouble(forFieldTextField.getText()) < 0)
			forFieldTextField.setBackground(Color.red);
		else if (forField != pop.getForField())
			forFieldTextField.setBackground(Color.yellow);
		else
			forFieldTextField.setBackground(Color.white);
	}

	private void colorAgainstFieldField() {
		if (Double.parseDouble(againstFieldTextField.getText()) > 1)
			againstFieldTextField.setBackground(Color.red);
		else if (Double.parseDouble(againstFieldTextField.getText()) < 0)
			againstFieldTextField.setBackground(Color.red);
		else if (againstField != pop.getAgainstField())
			againstFieldTextField.setBackground(Color.yellow);
		else
			againstFieldTextField.setBackground(Color.white);
	}

	private void colorForZealotTextField() {
		if (Integer.parseInt(forZealotTextField.getText()) > 100)
			forZealotTextField.setBackground(Color.red);
		else if (Integer.parseInt(forZealotTextField.getText()) < 0)
			forZealotTextField.setBackground(Color.red);
		else if (pop.getForZealot() != Integer.parseInt(forZealotTextField.getText()))
			forZealotTextField.setBackground(Color.yellow);
		else
			forZealotTextField.setBackground(Color.white);
	}

	private void colorAgainstZealotTextField() {
		if (Integer.parseInt(againstZealotTextField.getText()) > 100)
			againstZealotTextField.setBackground(Color.red);
		else if (Integer.parseInt(againstZealotTextField.getText()) < 0)
			againstZealotTextField.setBackground(Color.red);
		else if (pop.getAgainstZealot() != Integer.parseInt(againstZealotTextField.getText()))
			againstZealotTextField.setBackground(Color.yellow);
		else
			againstZealotTextField.setBackground(Color.white);
	}

	private void colorForChanceTextField() {
		if (Double.parseDouble(forChanceTextField.getText()) > 1)
			forChanceTextField.setBackground(Color.red);
		else if (Double.parseDouble(forChanceTextField.getText()) < 0)
			forChanceTextField.setBackground(Color.red);
		else if (forChance != pop.getForChance())
			forChanceTextField.setBackground(Color.yellow);
		else
			forChanceTextField.setBackground(Color.white);
	}

	private void colorAgainstChanceTextField() {
		if (Double.parseDouble(againstChanceTextField.getText()) > 1)
			againstChanceTextField.setBackground(Color.red);
		else if (Double.parseDouble(againstChanceTextField.getText()) < 0)
			againstChanceTextField.setBackground(Color.red);
		else if (againstChance != pop.getAgainstChance())
			againstChanceTextField.setBackground(Color.yellow);
		else
			againstChanceTextField.setBackground(Color.white);
	}

	private void colorAllFields() {
		populationCountTextField.setBackground(Color.white);
		speedTextField.setBackground(Color.white);
		initForTextField.setBackground(Color.white);
		forFieldTextField.setBackground(Color.white);
		againstFieldTextField.setBackground(Color.white);
		forZealotTextField.setBackground(Color.white);
		againstZealotTextField.setBackground(Color.white);
		forChanceTextField.setBackground(Color.white);
		againstChanceTextField.setBackground(Color.white);
	}

	private int getNumberOfAgents() {
		return this.numberOfAgents;
	}

	private void setNumberOfAgents(int numberOfAgents) {
		this.numberOfAgents = numberOfAgents;
	}

	private void incrementNumberOfAgents() {
		if (numberOfAgents + 10 < 1001)
			setNumberOfAgents(Integer.parseInt(populationCountTextField.getText()) + 10);
		else
			numberOfAgents = 1000;
	}

	public void decrementNumberOfAgents() {
		if (2 < numberOfAgents - 10)
			setNumberOfAgents(Integer.parseInt(populationCountTextField.getText()) - 10);
		else
			numberOfAgents = 3;
	}

	private void incrementInitialFor() {
		if (initialFor + 0.01 <= 1)
			initialFor = Double.parseDouble(initForTextField.getText()) + 0.01;
		else
			initialFor = 1;
	}

	private void decrementInitialFor() {
		if (0 <= initialFor - 0.01)
			initialFor = Double.parseDouble(initForTextField.getText()) - 0.01;
		else
			initialFor = 0;
	}

	private void incrementForZealots() {
		if (forZealots < numberOfAgents)
			forZealots++;
		else
			forZealots = numberOfAgents;
	}

	public void decrementForZealots() {
		if (0 < forZealots - 1)
			forZealots--;
		else
			forZealots = 0;
	}

	private void incrementAgainstZealots() {
		if (againstZealots < numberOfAgents)
			againstZealots++;
		else
			againstZealots = numberOfAgents;
	}

	public void decrementAgainstZealots() {
		if (0 < againstZealots - 1)
			againstZealots--;
		else
			againstZealots = 0;
	}

	private void incrementForChance() {
		if (forChance + 0.01 < 1)
			forChance += 0.01;
		else
			forChance = 1;
	}

	public void decrementForChance() {
		if (0 < forChance - 0.01)
			forChance -= 0.01;
		else
			forChance = 0;
	}

	private void incrementAgainstChance() {
		if (againstChance + 0.01 < 1)
			againstChance += 0.01;
		else
			againstChance = 1;
	}

	public void decrementAgainstChance() {
		if (0 < againstChance - 0.01)
			againstChance -= 0.01;
		else
			againstChance = 0;
	}

	private void incrementForField() {
		if (forField + 0.01 <= 1)
			forField = Double.parseDouble(forFieldTextField.getText()) + 0.01;
		else
			forField = 1;
	}

	private void decrementForField() {
		if (0 <= forField - 0.01)
			forField = Double.parseDouble(forFieldTextField.getText()) - 0.01;
		else
			forField = 0;
	}

	private void incrementAgainstField() {
		if (againstField + 0.01 <= 1)
			againstField = Double.parseDouble(againstFieldTextField.getText()) + 0.01;
		else
			againstField = 1;
	}

	private void decrementAgainstField() {
		if (0 <= againstField - 0.01)
			againstField = Double.parseDouble(againstFieldTextField.getText()) - 0.01;
		else
			againstField = 0;
	}

	private void setMode(Mode mode) {
		pop.setMode(mode);
		agentsPane.setMode(mode);
		chartPane.setMode(mode);
	}

	private void cleanQueues() {
		chartQueue.clear();
		opinionIndexesQueue.clear();
		controlSimFlow.clear();
		updateChart.clear();
		graphQueue.clear();
		nextStep.clear();
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

}
