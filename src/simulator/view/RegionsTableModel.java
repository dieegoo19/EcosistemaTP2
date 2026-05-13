package simulator.view;

import java.util.ArrayList;
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

public class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {

	// definir atributos necesarios
	private List<Info> info;
	private Controller ctrl;
	
	private class Info{
		int row;
		int col;
		String desc;
		Map<Animal.Diet, Integer> animalsPerDiet;
		
		Info(int row, int col, String desc, Map<Animal.Diet, Integer> miMapa){
			this.row = row;
			this.col = col;
			this.desc = desc;
			this.animalsPerDiet = miMapa;
		}
	}
	
	
	RegionsTableModel(Controller ctrl) {
		// inicializar estructuras de datos correspondientes
		this.ctrl = ctrl;
		this.info = new ArrayList<Info>();
		// registrar this como observador
		this.ctrl.addObserver(this);
	}

	// el resto de métodos van aquí…

	@Override
	public int getRowCount() {
		return info == null ? 0 : info.size();
	}

	@Override
	public int getColumnCount() {
		return 3 + Animal.Diet.values().length;
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0)
			return "Row";
		else if (col == 1)
			return "Col";
		else if (col == 2)
			return "Desc.";
		else
			return Animal.Diet.values()[col - 3].toString();
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return info.get(rowIndex).row;
		if (columnIndex == 1)
			return info.get(rowIndex).col;
		if (columnIndex == 2)
			return info.get(rowIndex).desc;
		Animal.Diet[] diets = Animal.Diet.values();
		return info.get(rowIndex).animalsPerDiet.get(diets[columnIndex - 3]);
	}
	
	private void update(MapInfo map) {
	    List<Info> newInfo = new ArrayList<>();
	    for (MapInfo.RegionData reg : map) {
	        Map<Animal.Diet, Integer> dietCounts = new HashMap<>();
	        for (Animal.Diet diet : Animal.Diet.values()) {
	            dietCounts.put(diet, 0);
	        }
	        for (AnimalInfo a : reg.r().getAnimalsInfo()) {
	            Animal.Diet diet = a.getDiet();
	            dietCounts.put(diet, dietCounts.get(diet) + 1);
	        }
	        newInfo.add(new Info(reg.row(), reg.col(), reg.r().toString(), dietCounts));
	    }
	    this.info = newInfo;
	}
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		update(map);
		this.fireTableDataChanged();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		update(map);
		this.fireTableDataChanged();
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		update(map);
		this.fireTableDataChanged();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		update(map);
		this.fireTableDataChanged();
	}

}
