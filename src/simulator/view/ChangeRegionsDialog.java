package simulator.view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;
import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.*;

public class ChangeRegionsDialog extends JDialog implements EcoSysObserver {

	private DefaultComboBoxModel<String> regionsModel;
	private DefaultComboBoxModel<String> fromRowModel;
	private DefaultComboBoxModel<String> toRowModel;
	private DefaultComboBoxModel<String> fromColModel;
	private DefaultComboBoxModel<String> toColModel;

	private DefaultTableModel dataTableModel;
	private Controller ctrl;
	private List<JSONObject> regionsInfo;
	private JComboBox<String> regionsComboBox;

	private String[] headers = { "Key", "Value", "Description" };

	ChangeRegionsDialog(Controller ctrl) {
		super((Frame) null, true);
		this.ctrl = ctrl;
		initGUI();
		this.ctrl.addObserver(this);
	}

	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		// Texto de ayuda
		JLabel helpText = new JLabel("<html><p style='margin: 10px;'>Select a region type, "
				+ "the rows/cols range, and provide values for the parameters in the <b>Value column</b> "
				+ "(default values are used for empty fields).</p></html>");
		helpText.setFont(helpText.getFont().deriveFont(java.awt.Font.PLAIN));
		helpText.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(helpText);

		// Tabla
		dataTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1; // Solo la columna Value
			}
		};
		dataTableModel.setColumnIdentifiers(headers);
		JTable table = new JTable(dataTableModel);
		mainPanel.add(new JScrollPane(table));

		// Panel de selección
		this.regionsInfo = Main.regionsFactory.getInfo();
		regionsModel = new DefaultComboBoxModel<>();
		for (JSONObject info : regionsInfo) {
			regionsModel.addElement(info.getString("type"));
		}

		JPanel selectionPanel = new JPanel();
		mainPanel.add(selectionPanel);

		regionsComboBox = new JComboBox<>(regionsModel);
		regionsComboBox.addActionListener(e -> updateTableModel(regionsComboBox.getSelectedIndex()));
		selectionPanel.add(new JLabel("Region type: "));
		selectionPanel.add(regionsComboBox);

		// Modelos de coordenadas
		fromRowModel = new DefaultComboBoxModel<>();
		toRowModel = new DefaultComboBoxModel<>();
		fromColModel = new DefaultComboBoxModel<>();
		toColModel = new DefaultComboBoxModel<>();

		selectionPanel.add(new JLabel("Row from/to: "));
		selectionPanel.add(new JComboBox<>(fromRowModel));
		selectionPanel.add(new JComboBox<>(toRowModel));
		selectionPanel.add(new JLabel("Column from/to: "));
		selectionPanel.add(new JComboBox<>(fromColModel));
		selectionPanel.add(new JComboBox<>(toColModel));

		// Botones
		JPanel buttonsPanel = new JPanel();
		mainPanel.add(buttonsPanel);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> setVisible(false));
		buttonsPanel.add(cancelButton);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> handleOk());
		buttonsPanel.add(okButton);

		if (!regionsInfo.isEmpty())
			updateTableModel(0);

		setPreferredSize(new Dimension(750, 450));
		pack();
		setResizable(false);
		setVisible(false);
	}

	public void open(Frame parent) {
		setLocation(parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

	private void updateTableModel(int index) {
		dataTableModel.setRowCount(0);
		JSONObject data = regionsInfo.get(index).getJSONObject("data");
		for (String key : data.keySet()) {
			dataTableModel.addRow(new Object[] { key, "", data.getString(key) });
		}
	}

	private void handleOk() {
		try {
			JSONObject regionData = new JSONObject();
			for (int i = 0; i < dataTableModel.getRowCount(); i++) {
				String key = (String) dataTableModel.getValueAt(i, 0);
				String val = (String) dataTableModel.getValueAt(i, 1);
				if (val != null && val != "") {
					if (Double.parseDouble(val) >= 0)
						regionData.put(key, val);
				}
			}

			JSONObject spec = new JSONObject();
			spec.put("type", regionsModel.getElementAt(regionsComboBox.getSelectedIndex()));
			spec.put("data", regionData);

			JSONObject regionEntry = new JSONObject();
			regionEntry.put("row", new JSONArray().put(Integer.parseInt((String) fromRowModel.getSelectedItem()))
					.put(Integer.parseInt((String) toRowModel.getSelectedItem())));
			regionEntry.put("col", new JSONArray().put(Integer.parseInt((String) fromColModel.getSelectedItem()))
					.put(Integer.parseInt((String) toColModel.getSelectedItem())));
			regionEntry.put("spec", spec);

			JSONObject finalRS = new JSONObject().put("regions", new JSONArray().put(regionEntry));

			ctrl.setRegions(finalRS);
			setVisible(false);
		} catch (Exception e) {
			ViewUtils.showErrorMsg("Error setting regions: " + e.getMessage());
		}
	}

	private void updateCoords(MapInfo map) {
		fromRowModel.removeAllElements();
		toRowModel.removeAllElements();
		fromColModel.removeAllElements();
		toColModel.removeAllElements();
		for (int i = 0; i < map.getRows(); i++) {
			fromRowModel.addElement("" + i);
			toRowModel.addElement("" + i);
		}
		for (int i = 0; i < map.getCols(); i++) {
			fromColModel.addElement("" + i);
			toColModel.addElement("" + i);
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			updateCoords(map);
		});
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			updateCoords(map);
		});
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
	}

}