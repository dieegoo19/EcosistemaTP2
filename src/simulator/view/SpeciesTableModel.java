/**
 * 
 */
package simulator.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		if(columnIndex == 0) return specie;
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
		update(animals);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		update(animals);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		update(animals);
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		update(animals);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	private void update(List<AnimalInfo> animals) {
	    Map<String, Map<Animal.State, Integer>> newInfo = new HashMap<>();
	    for (AnimalInfo a : animals) {
	        String sp = a.getGeneticCode();
	        Animal.State st = a.getState();
	        if (!newInfo.containsKey(sp)) {
	            Map<Animal.State, Integer> initialCounts = new HashMap<>();
	            for (Animal.State state : Animal.State.values()) {
	                initialCounts.put(state, 0);
	            }
	            newInfo.put(sp, initialCounts);
	        }
	        Map<Animal.State, Integer> counts = newInfo.get(sp);
	        counts.put(st, counts.get(st) + 1);
	    }
	    this.info = newInfo;
	    this.speciesNames = new ArrayList<>(this.info.keySet());
	    Collections.sort(this.speciesNames);
	    this.fireTableDataChanged();
	}
}
