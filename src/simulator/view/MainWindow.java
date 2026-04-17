package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import simulator.control.Controller;

public class MainWindow extends JFrame {

	private Controller ctrl;

	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		this.ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);

		// crear ControlPanel y añadirlo en PAGE_START de mainPanel
		ControlPanel cp = new ControlPanel(ctrl);
		mainPanel.add(cp, BorderLayout.PAGE_START);
		// crear StatusBar y añadirlo en PAGE_END de mainPanel
		StatusBar st = new StatusBar(ctrl);
		mainPanel.add(st, BorderLayout.PAGE_END);
		// Definición del panel de tablas (usa un BoxLayout vertical)
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);

		// crear la tabla de especies y añadirla a contentPanel.
		// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
		InfoTable speciesTable = new InfoTable("Species", new SpeciesTableModel(this.ctrl));
		speciesTable.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(speciesTable);
		// crear la tabla de regiones.
		// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
		InfoTable regTable = new InfoTable("Regions", new RegionsTableModel(this.ctrl));
		regTable.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(regTable);
		// llama a ViewUtils.quit(MainWindow.this) en el método windowClosing
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ViewUtils.quit(MainWindow.this);
			}
		});

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);
	}
}
