package fr.ubordeaux.ao.mazing.view.swing;

import javax.swing.JFrame;
import java.awt.Dimension;

public class ViewWindow extends JFrame {
	public final ViewPanel panel;

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
}