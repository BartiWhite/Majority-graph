package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Circle {
	private final int X, Y, R;
	private Color color, ovalColor = Color.black;

	public Circle(int X, int Y, int R, Color color) {
		this.X = X;
		this.Y = Y;
		this.R = R;
		this.color = color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setOvalColor(Color ovalColor) {
		this.ovalColor = ovalColor;
	}

	public void draw(Graphics2D g) {
		g.setColor(color);
		g.fillOval(X, Y, R, R);
		g.setColor(ovalColor);
		Ellipse2D.Double circle = new Ellipse2D.Double(X, Y, R, R);
		g.draw(circle);
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}
}
