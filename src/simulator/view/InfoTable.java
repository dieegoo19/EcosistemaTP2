package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {

	  private String title;  
	  private TableModel tableModel;

	  InfoTable(String title, TableModel tableModel) {  
	    this.title = title;  
	    this.tableModel = tableModel;  
	    initGUI();  
	  }

	  private void initGUI() {
	    // cambiar el layout del panel a BorderLayout()
		  setLayout(new BorderLayout());  
	    // añadir un borde con título al JPanel, con el texto this.title
		  setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
				  this.title, TitledBorder.LEFT, TitledBorder.TOP));
	    // añadir un JTable (con barra de desplazamiento vertical) que use this.tableModel
		  JTable tabla = new JTable(this.tableModel);
		  this.add(new JScrollPane(tabla), BorderLayout.CENTER);
	  }
}
