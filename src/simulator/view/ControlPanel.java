package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.json.JSONObject;
import org.json.JSONTokener;

import icons.ICONS;
import simulator.control.Controller;
import simulator.launcher.Main;

class ControlPanel extends JPanel {

	private Controller ctrl;
	private ChangeRegionsDialog changeRegionsDialog;

	private JToolBar toolaBar;
	private JFileChooser fc;
	private boolean stopped = true; // utilizado en los botones de run/stop
	private JButton openButton;
	private JButton viewerButton;
	private JButton regionsButton;
	private JButton runButton;
	private JButton stopButton;
	private JButton quitButton;

	private JSpinner stepsSpinner;
	private JTextField deltaText;

	ControlPanel(Controller ctrl) {
		this.ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		toolaBar = new JToolBar();
		add(toolaBar, BorderLayout.PAGE_START);

		// crear los diferentes botones/atributos y añadirlos a la toolBar.
		// Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
		// this.toolaBar.addSeparator() para añadir la línea de separación vertical
		// entre las componentes que lo necesiten.

		// Open Button
		this.openButton = new JButton();
		this.openButton.setToolTipText("Open");
		this.openButton.setIcon(new ImageIcon(ICONS.class.getResource("open.png")));
		this.openButton.addActionListener((e) -> {
			if (this.fc.showOpenDialog(ViewUtils.getWindow(this)) == JFileChooser.APPROVE_OPTION) {
				File file = this.fc.getSelectedFile();
				try {
					JSONObject jo = new JSONObject(new JSONTokener(new FileInputStream(file)));

					int cols = jo.getInt("cols");
					int rows = jo.getInt("rows");
					int width = jo.getInt("width");
					int height = jo.getInt("height");

					ctrl.reset(cols, rows, width, height);
					ctrl.loadData(jo);

				} catch (Exception ex) {
					ViewUtils.showErrorMsg("Error al cargar el archivo " + ex.getMessage());
				}
			}
		});
		this.toolaBar.add(openButton);

		// Viewer Button
		this.toolaBar.addSeparator();
		this.viewerButton = new JButton();
		this.viewerButton.setToolTipText("Viewer");
		this.viewerButton.setIcon(new ImageIcon(ICONS.class.getResource("viewer.png")));
		this.viewerButton.addActionListener(e -> new MapWindow(ViewUtils.getWindow(this), ctrl));
		this.toolaBar.add(viewerButton);

		// Regions Button
		this.regionsButton = new JButton();
		this.regionsButton.setToolTipText("Regions");
		this.regionsButton.setIcon(new ImageIcon(ICONS.class.getResource("regions.png")));
		this.regionsButton.addActionListener(e -> this.changeRegionsDialog.open(ViewUtils.getWindow(this)));
		this.toolaBar.add(regionsButton);

		// Botón Run
		this.toolaBar.addSeparator();
		this.runButton = new JButton();
		this.runButton.setToolTipText("Run");
		this.runButton.setIcon(new ImageIcon(ICONS.class.getResource("run.png")));
		this.runButton.addActionListener(e -> {
			try {
				this.stopped = false;
				this.setEnableButtons(false);
				runSim((Integer) this.stepsSpinner.getValue(), Double.parseDouble(this.deltaText.getText())); // Lanzamos la ejecución
			} catch (NumberFormatException nfe) {
				ViewUtils.showErrorMsg(nfe.getMessage());
				this.stopped = true;
				setEnableButtons(true);
			}
		});
		this.toolaBar.add(runButton);

		// Botón Stop
		this.stopButton = new JButton();
		this.stopButton.setToolTipText("Stop");
		this.stopButton.setIcon(new ImageIcon(ICONS.class.getResource("stop.png")));
		this.stopButton.addActionListener(e -> {
			this.stopped = true;
			this.setEnableButtons(true);
		});
		this.stopButton.setEnabled(false);
		this.toolaBar.add(stopButton);

		// Spinner Steps
		JLabel steps = new JLabel("Steps: ");
		this.toolaBar.add(steps);
		stepsSpinner = new JSpinner(new SpinnerNumberModel(5000, 1, 10000, 100));
		stepsSpinner.setToolTipText("Simulation steps to run: 1-10000");
		stepsSpinner.setMaximumSize(new Dimension(80, 40));
		stepsSpinner.setMinimumSize(new Dimension(80, 40));
		stepsSpinner.setPreferredSize(new Dimension(80, 40));
		toolaBar.add(stepsSpinner);

		// Text Delta-Time
		JLabel dt = new JLabel("Delta-Time: ");
		this.toolaBar.add(dt);
		deltaText = new JTextField();
		deltaText.setToolTipText("Delta Time");
		deltaText.setText("" + Main.deltaTime);
		deltaText.setMaximumSize(new Dimension(80, 40));
		deltaText.setMinimumSize(new Dimension(80, 40));
		deltaText.setPreferredSize(new Dimension(80, 40));
		toolaBar.add(deltaText);

		// Quit Button
		this.toolaBar.add(Box.createGlue()); // this aligns the button to the right
		this.toolaBar.addSeparator();
		this.quitButton = new JButton();
		this.quitButton.setToolTipText("Quit");
		this.quitButton.setIcon(new ImageIcon(ICONS.class.getResource("exit.png")));
		this.quitButton.addActionListener(e -> ViewUtils.quit(this));
		this.toolaBar.add(quitButton);

		// Inicializar this.fc con una instancia de JFileChooser. Para que siempre
		// abre en la carpeta de ejemplos puedes usar:
		this.fc = new JFileChooser();
		this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		// Inicializar this.changeRegionsDialog con instancias del diálogo de cambio de
		// regiones
		// crear ChangeRegionsDialog.java
		this.changeRegionsDialog = new ChangeRegionsDialog(ctrl);
		
	}

	private void runSim(int n, double dt) {
		if (n > 0 && !this.stopped) {
			try {
				this.ctrl.advance(dt);
				SwingUtilities.invokeLater(() -> runSim(n - 1, dt));
			} catch (Exception e) {
				// llamar a ViewUtils.showErrorMsg con el mensaje de error que corresponda
				ViewUtils.showErrorMsg(e.getMessage());
				// activar todos los botones
				setEnableButtons(true);
				this.stopped = true;
			}
		}
	}

	private void setEnableButtons(boolean enable) {
		this.openButton.setEnabled(enable);
		this.viewerButton.setEnabled(enable);
		this.regionsButton.setEnabled(enable);
		this.runButton.setEnabled(enable);
		this.quitButton.setEnabled(enable);
		this.stepsSpinner.setEnabled(enable);
		this.deltaText.setEnabled(enable);

		this.stopButton.setEnabled(!enable);
	}
}
