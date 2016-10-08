package edu.cmu.pact.miss.MetaTutor;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.border.Border;

public class FancyBorder implements Border {
	
	private final Image image;
	
	public FancyBorder(Image img) {
		this.image = img;
	}
	
	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, 0);
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		int x0 = x +60;
		int y0 = y + 0;
		g.drawImage(image, x0, y0, null);
	}

}
