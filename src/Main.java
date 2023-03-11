import javax.swing.SwingUtilities;

public class Main {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {
			GraphicFrame frame = new GraphicFrame();
			frame.setVisible(true);
		});
	}
}