package simulator.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {

	// definir atributos necesarios
	private Map<String, Map<Animal.State, Integer>> info;
	private List<String> speciesNames;
	private Controller ctrl;

	SpeciesTableModel(Controller ctrl) {
		// inicializar estructuras de datos correspondientes
		this.ctrl = ctrl;
		this.info = new HashMap<>();
		this.speciesNames = new ArrayList<>();
		// registrar this como observador
		this.ctrl.addObserver(this);
	}

	// el resto de métodos van aquí …

	@Override
	public int getRowCount() {
		return this.info.size();
	}

	@Override
	public int getColumnCount() {
		return Animal.State.values().length + 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String specie = speciesNames.get(rowIndex);
		if (columnIndex == 0)
			return specie;
		Animal.State[] states = Animal.State.values();
		return info.get(specie).get(states[columnIndex - 1]);
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0)
			return "Species";
		else
			return Animal.State.values()[columnIndex - 1].toString();
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			update(animals);
		});
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			update(animals);
		});
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		SwingUtilities.invokeLater(() -> {
			update(animals);
		});
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater(() -> {
			update(animals);
		});
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	private void update(List<AnimalInfo> animals) {
		this.speciesNames = new ArrayList<>();
		this.info = new HashMap<>();
		for (AnimalInfo a : animals) {
			if (!speciesNames.contains(a.getGeneticCode())) {
				speciesNames.add(a.getGeneticCode());
			}
		}
		Collections.sort(speciesNames);
		Animal.State[] states = Animal.State.values();
		for (String sp : speciesNames) {
			Map<Animal.State, Integer> newMap = new HashMap<>();
			for (Animal.State st : states) {
				newMap.put(st,
						(int) animals.stream().filter(a -> a.getGeneticCode() == sp && a.getState() == st).count());
			}
			info.put(sp, newMap);
		}
		this.fireTableDataChanged();
	}
}
