import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartPane extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	XYSeries forSeries, againstSeries, expectedSeries;
	XYSeriesCollection collection;
	JFreeChart chart;
	ChartPanel chartPanel;
	private BlockingQueue<List<Opinion>> queue;
	private BlockingQueue<Boolean> updateChart = new ArrayBlockingQueue<>(1);
	private List<Opinion> opinion = new ArrayList<>();
	private int forAgents = 10, againstAgents = 10, forZealots = 0, againstZealots = 0, time = 0;
	private double forField = 0, againstField = 0, forChance = 1, againstChance = 1, initialFor = 0.5, expectedValue,
			previousValue = initialFor;
	private Mode mode = Mode.FLUENT;

	public ChartPane(BlockingQueue<List<Opinion>> queue, BlockingQueue<Boolean> updateChart) {
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(900, 200));
		this.queue = queue;
		this.updateChart = updateChart;

		forSeries = new XYSeries("For", true, true);
		againstSeries = new XYSeries("Against", true, true);
		expectedSeries = new XYSeries("Expected", true, true);

		collection = new XYSeriesCollection(againstSeries);
		collection.addSeries(forSeries);
		collection.addSeries(expectedSeries);

		chart = ChartFactory.createXYLineChart("", // title
				"time", // OX axis
				"opinion", // OY axis
				collection, // database
				PlotOrientation.VERTICAL, // orientation
				true, // legend
				false, // tooltips
				false); // urls
		chart.setBorderPaint(Color.green);
		chartPanel = new ChartPanel(chart);
		this.add(chartPanel);
	}

	@Override
	public void run() {

		while (true) {
			try {
				opinion = queue.take();
				Boolean receiveMessage;
				if (mode == Mode.STEPOVER)
					receiveMessage = updateChart.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			forAgents = 0;
			againstAgents = 0;

			for (Opinion i : opinion)
				if (i.opinionValue == 1)
					forAgents++;
				else
					againstAgents++;

			List<Person> pop = Person.getPeople();
//			for (Person per : pop) {
//				if (per.getOpinion() == Opinion.FOR)
//					;
//			}

			if (forChance == 1 && againstChance == 1 && forZealots == 0 && againstZealots == 0) {
				countExpectedValue(1);
			} else if (forField == 0 && againstField == 0 && forZealots == 0 && againstZealots == 0) {
				countExpectedValue(2);
			}

			else if (forField == 0 && againstField == 0 && forChance == 1 && againstChance == 1) {
				countExpectedValue(3);
			} else {
				countExpectedValue(0);
			}

			if (!opinion.isEmpty()) {
				forSeries.add(time, forAgents);
				againstSeries.add(time, againstAgents);
				expectedSeries.add(time, expectedValue * pop.size());
				time++;
			}
		}
	}

	public void restart() {
		forSeries.clear();
		againstSeries.clear();
		expectedSeries.clear();
		opinion.clear();
		expectedValue = initialFor;
		previousValue = initialFor;
		time = 0;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	private void countExpectedValue(int formula) {
		if (formula == 0) {
			expectedValue = 0;
		}
		if (formula == 1) {
			expectedValue = previousValue * (1 - previousValue) * (2 * previousValue - 1)
					+ forField * (1 - previousValue) - againstField * previousValue + previousValue;
			previousValue = expectedValue;
		}
		if (formula == 2) {
			expectedValue = previousValue * (1 - previousValue)
					* ((forChance + againstChance) * previousValue - againstChance) + previousValue;
			previousValue = expectedValue;
		}
		if (formula == 3) {
			expectedValue = previousValue * previousValue
					* (1 - previousValue + againstZealots / Person.getPeople().size())
					- (previousValue - forZealots / Person.getPeople().size()) * (1 - previousValue)
							* (1 - previousValue)
					+ previousValue;
			previousValue = expectedValue;
		}

	}

	public void setParameters(double initialFor, double forField, double againstField, double forChance,
							  double againstChance, int forZealots, int againstZealots) {
		this.initialFor = initialFor;
		this.forChance = forChance;
		this.againstChance = againstChance;
		this.forField = forField;
		this.againstField = againstField;
		this.forZealots = forZealots;
		this.againstZealots = againstZealots;
	}

}