package simulator.view;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {

	// definir atributos necesarios
	private MapInfo map;
	private Controller ctrl;

	RegionsTableModel(Controller ctrl) {
		// inicializar estructuras de datos correspondientes
		this.ctrl = ctrl;
		// registrar this como observador
		this.ctrl.addObserver(this);
	}

	// el resto de métodos van aquí…

	@Override
	public int getRowCount() {
		return map == null ? 0 : map.getCols() * map.getRows();
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
		int row = rowIndex / map.getCols();
		int col = rowIndex % map.getCols();

		RegionInfo region = null;
		for (MapInfo.RegionData it : map) { // Iterador
			if (it.row() == row && it.col() == col) {
				region = it.r();
				break;
			}
		}

		if (columnIndex == 0)
			return row;
		if (columnIndex == 1)
			return col;
		if (columnIndex == 2)
			return region.toString();

		Animal.Diet diet = Animal.Diet.values()[columnIndex - 3];
		int cont = (int) region.getAnimalsInfo().stream().filter(a -> a.getDiet() == diet).count();
		return cont;
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
		this.map = map;
		this.fireTableDataChanged();
		});
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
		this.map = map;
		this.fireTableDataChanged();
		});
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater(() -> {
		this.fireTableDataChanged();
		});
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		SwingUtilities.invokeLater(() -> {
		this.fireTableDataChanged();
		});
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		SwingUtilities.invokeLater(() -> {
		this.fireTableDataChanged();
		});
	}

}
