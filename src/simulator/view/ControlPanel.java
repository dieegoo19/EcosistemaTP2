package simulator.view;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;

class ControlPanel extends JPanel {
	
	private Controller ctrl;  
	private ChangeRegionsDialog changeRegionsDialog;

	private JToolBar toolBar;  
    private JFileChooser fc;  
    private boolean stopped = true;
    private JButton quitButton;
    
    private JButton openButton;
    private JButton viewerButton;
    private JButton regionsButton;
    private JButton runButton;
    private JButton stopButton;
    
    private JSpinner stepsSpinner;
    private JTextField deltaText;
    
    ControlPanel(Controller ctrl) {  
    	this.ctrl = ctrl;  
    	initGUI();  
    }
    
    private void loadFile() {
    	if(this.fc.showOpenDialog(ViewUtils.getWindow(this)) == JFileChooser.APPROVE_OPTION) {
			File file = this.fc.getSelectedFile();
			try (FileInputStream fis = new FileInputStream(file)){
				JSONObject jo = new JSONObject(new JSONTokener(fis));
				
				int cols = jo.getInt("cols");
				int rows = jo.getInt("rows");
				int width = jo.getInt("width");
				int height = jo.getInt("height");
				
				ctrl.reset(cols, rows, width, height);
				ctrl.loadData(jo);
				
			}
			catch (Exception ex) {
				ViewUtils.showErrorMsg("Error al cargar el archivo " + ex.getMessage());
			}
		}
    }
    
    private void initGUI() {  
    	setLayout(new BorderLayout());  
    	toolBar = new JToolBar();  
    	add(toolBar, BorderLayout.PAGE_START);
    	
    	this.fc = new JFileChooser();
    	this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
    	this.changeRegionsDialog = new ChangeRegionsDialog(ctrl);
    	
    	// Open Button
    	//TODO this.toolaBar.add(Box.createGlue()); // this aligns the button to the right  
    	this.openButton = new JButton();
    	this.openButton.setToolTipText("Open");
    	this.openButton.setIcon(new ImageIcon("resources/icons/open.png"));
    	this.openButton.addActionListener(e -> loadFile());
    	this.toolBar.add(openButton);
    	
    	this.toolBar.addSeparator();
    	
    	// Viewer Button
    	this.viewerButton = new JButton();
    	this.viewerButton.setToolTipText("Viewer");
    	this.viewerButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
    	this.viewerButton.addActionListener(e -> new MapWindow(ViewUtils.getWindow(this), ctrl));
    	this.toolBar.add(viewerButton);
    	
    	// Regions Button
    	this.regionsButton = new JButton();
    	this.regionsButton.setToolTipText("Regions");
    	this.regionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
    	this.regionsButton.addActionListener(e -> this.changeRegionsDialog.open(ViewUtils.getWindow(this)));
    	this.toolBar.add(regionsButton);
    	
    	this.toolBar.addSeparator();  
    	
    	// Botón Run
    	this.runButton = new JButton();
    	this.runButton.setToolTipText("Run");
    	this.runButton.setIcon(new ImageIcon("resources/icons/run.png"));
    	this.runButton.addActionListener((e) -> {
    		try {
    			this.stopped = false;
    			this.setEnableButtons(false);
    			runSim((Integer)this.stepsSpinner.getValue(), Double.parseDouble(this.deltaText.getText())); // Lanzamos la ejecución
    		}
    		catch (NumberFormatException nfe){
    			ViewUtils.showErrorMsg(nfe.getMessage());
    			this.stopped = true;
    			setEnableButtons(true);
    		}
    	});
    	this.toolBar.add(runButton);
    	
    	// Botón Stop  
    	this.stopButton = new JButton();
    	this.stopButton.setToolTipText("Stop");
    	this.stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
    	this.stopButton.addActionListener((e) -> {
    		this.stopped = true;
    	});
    	this.toolBar.add(stopButton);
    	
    	// TODO text fill con jspinner: steps y delta time
    	
    	
    	// Quit Button  
    	this.toolBar.add(Box.createGlue()); // this aligns the button to the right  
    	this.toolBar.addSeparator();  
    	this.quitButton = new JButton();  
    	this.quitButton.setToolTipText("Quit");  
    	this.quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));  
    	this.quitButton.addActionListener((e) -> ViewUtils.quit(this));  
    	this.toolBar.add(quitButton);
    	
    	
    }   


    private void runSim(int n, double dt) {  
    	if (n > 0 && !this.stopped) {  
    		try {  
    			this.ctrl.advance(dt);  
    			SwingUtilities.invokeLater(() -> runSim(n - 1, dt));  
    		}
    		catch (Exception e) {  
    			// llamar a ViewUtils.showErrorMsg con el mensaje de error que corresponda
    			ViewUtils.showErrorMsg(e.getMessage());
    			// activar todos los botones
    			setEnableButtons(true);
    			this.stopped = true;  
    		}  
    	} else {
    		// activar todos los botones
    		setEnableButtons(true);
    		this.stopped = true;  
    	}  
    }
    
    private void setEnableButtons(boolean enable) {
    	this.openButton.setEnabled(enable);
    	this.viewerButton.setEnabled(enable);
    	this.regionsButton.setEnabled(enable);
    	this.runButton.setEnabled(enable);
    	this.quitButton.setEnabled(enable);
    	
    	this.stopButton.setEnabled(!enable);
    }
}
