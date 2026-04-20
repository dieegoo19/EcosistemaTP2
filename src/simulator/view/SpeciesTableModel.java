/**
 * 
 */
package simulator.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private List<AnimalInfo> animals;
	private List<String> speciesNames;
	private Controller ctrl;

	SpeciesTableModel(Controller ctrl) {
		// inicializar estructuras de datos correspondientes
		this.ctrl = ctrl;
		this.animals = new ArrayList<>();
		this.speciesNames = new ArrayList<>();
		// registrar this como observador
		this.ctrl.addObserver(this);
	}

	// el resto de métodos van aquí …

	@Override
	public int getRowCount() {
		return this.speciesNames.size();
	}

	@Override
	public int getColumnCount() {
		return Animal.State.values().length + 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String species = speciesNames.get(rowIndex);

		if (columnIndex == 0)
			return species;

		Animal.State state = Animal.State.values()[columnIndex - 1];
		int cont = (int) animals.stream().filter(a -> a.getGeneticCode().equals(species) && a.getState() == state)
				.count();
		return cont;
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
		this.animals = animals;
		this.speciesNames = new ArrayList<>();
		for (AnimalInfo a : animals) {
			if (!speciesNames.contains(a.getGeneticCode())) {
				speciesNames.add(a.getGeneticCode());
			}
		}
		Collections.sort(speciesNames);
		this.fireTableDataChanged();
	}
}
