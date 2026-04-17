package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.*;
import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatusBar extends JPanel implements EcoSysObserver {

	private JLabel timeLabel;
	private JLabel totalAnimalsLabel;
	private JLabel dimensionLabel;

	StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));

		timeLabel = new JLabel("Time: 0.000");
		totalAnimalsLabel = new JLabel("Total Animals: 0");
		dimensionLabel = new JLabel("Dimension: -");

		this.add(timeLabel);
		addSeparator();
		this.add(totalAnimalsLabel);
		addSeparator();
		this.add(dimensionLabel);
	}

	private void addSeparator() {
		JSeparator s = new JSeparator(JSeparator.VERTICAL);
		s.setPreferredSize(new Dimension(10, 20));
		this.add(s);
	}

	private void updateInfo(double time, MapInfo map, List<AnimalInfo> animals) {
		timeLabel.setText(String.format("Time: %.3f", time));
		totalAnimalsLabel.setText("Total Animals: " + animals.size());
		dimensionLabel.setText(
				String.format("Dimension: %dx%d %dx%d", map.getWidth(), map.getHeight(), map.getCols(), map.getRows()));
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> updateInfo(time, map, animals));
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> updateInfo(time, map, animals));
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater(() -> updateInfo(time, map, animals));
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		SwingUtilities.invokeLater(() -> updateInfo(time, map, animals));
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		SwingUtilities.invokeLater(() -> dimensionLabel.setText(String.format("Dimension: %dx%d %dx%d", map.getWidth(),
				map.getHeight(), map.getCols(), map.getRows())));
	}
}