package simulator.view;

import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatusBar extends JFrame implements EcoSysObserver {

	// TODO Añadir los atributos necesarios.  
	private Controller ctrl;
	
	StatusBar(Controller ctrl) {  
		this.ctrl = ctrl;
		initGUI();  
		// registrar this como observador
		ctrl.addObserver(this);
	}
	
	private void initGUI() {  
		this.setLayout(new FlowLayout(FlowLayout.LEFT));  
		this.setBorder(BorderFactory.createBevelBorder(1));
		
		// TODO Crear varios JLabel para el tiempo, el número de animales, y la
		//      dimensión y añadirlos al panel. Puedes utilizar el siguiente código
		//      para añadir un separador vertical:  
		//
		//     JSeparator s = new JSeparator(JSeparator.VERTICAL);  
		//     s.setPreferredSize(new Dimension(10, 20));  
		//     this.add(s);  
	}
	
	// TODO el resto de métodos van aquí… 
	
	public StatusBar() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}

	public StatusBar(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	public StatusBar(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public StatusBar(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub

	}

}
