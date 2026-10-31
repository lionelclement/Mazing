package fr.ubordeaux.ao.mazing.view.swing;

import javax.swing.JFrame;
import java.awt.Dimension;

/**
 * Represents ViewWindow in the rendering system.
 */
public class ViewWindow extends JFrame {
	/**
	 * Provides the private final viewpanel panel; value.
	 */
	private final ViewPanel panel;

	/**
	 * Creates a ViewWindow instance.
	 */
	public ViewWindow() {
		super("Isometric 3D Game");

		this.panel = new ViewPanel(1600, 1000);

		setContentPane(panel);

		setResizable(true);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1600, 1000));
		setSize(new Dimension(1600, 1000));
		pack();

	}
	/**
	 * Returns the panel.
	 * @return the resulting value
	 */
	public ViewPanel getPanel() {
		return panel;
	}
}