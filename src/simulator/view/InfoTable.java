package simulator.view;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;
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
	    // TODO añadir un borde con título al JPanel, con el texto this.title
	    // TODO añadir un JTable (con barra de desplazamiento vertical) que use this.tableModel
	  }
}
