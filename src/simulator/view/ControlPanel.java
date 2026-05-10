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
	
	// Atributos de los componentes
	private JButton openButton;
	private JButton viewerButton;
	private JButton regionsButton;
	private JButton runButton;
	private JButton stopButton;
	private JButton quitButton;

	private JSpinner stepsSpinner;
	private JTextField deltaText;
	private JSpinner delaySpinner;

	private volatile Thread thread;

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
		openButton = new JButton();
		openButton.setToolTipText("Open");
		openButton.setIcon(new ImageIcon(ICONS.class.getResource("open.png")));
		openButton.addActionListener((e) -> {
			if (fc.showOpenDialog(ViewUtils.getWindow(this)) == JFileChooser.APPROVE_OPTION) {
				try {
					JSONObject jo = new JSONObject(new JSONTokener(new FileInputStream(fc.getSelectedFile())));
					ctrl.reset(jo.getInt("cols"), jo.getInt("rows"), jo.getInt("width"), jo.getInt("height"));
					ctrl.loadData(jo);
				} catch (Exception ex) {
					ViewUtils.showErrorMsg("Error al cargar el archivo: " + ex.getMessage());
				}
			}
		});
		toolaBar.add(openButton);

		// Viewer Button
		toolaBar.addSeparator();
		viewerButton = new JButton();
		viewerButton.setToolTipText("Viewer");
		viewerButton.setIcon(new ImageIcon(ICONS.class.getResource("viewer.png")));
		viewerButton.addActionListener(e -> new MapWindow(ViewUtils.getWindow(this), ctrl));
		toolaBar.add(viewerButton);

		// Regions Button
		regionsButton = new JButton();
		regionsButton.setToolTipText("Regions");
		regionsButton.setIcon(new ImageIcon(ICONS.class.getResource("regions.png")));
		regionsButton.addActionListener(e -> changeRegionsDialog.open(ViewUtils.getWindow(this)));
		toolaBar.add(regionsButton);

		// Run Button
		toolaBar.addSeparator();
		runButton = new JButton();
		runButton.setToolTipText("Run");
		runButton.setIcon(new ImageIcon(ICONS.class.getResource("run.png")));
		runButton.addActionListener(e -> {
			try {
				int steps = (Integer) stepsSpinner.getValue();
				double dt = Double.parseDouble(deltaText.getText());
				int delayValue = (Integer) delaySpinner.getValue();

				setEnableButtons(false);
				
				thread = new Thread(() -> {
					try {
						runSim(steps, dt, delayValue);
					} finally {
						SwingUtilities.invokeLater(() -> {
							setEnableButtons(true);
							thread = null;
						});
					}
				});
				thread.start();

			} catch (NumberFormatException nfe) {
				ViewUtils.showErrorMsg("Delta-Time Invalido: " + deltaText.getText());
			}
		});
		toolaBar.add(runButton);

		// Stop Button
		stopButton = new JButton();
		stopButton.setToolTipText("Stop");
		stopButton.setIcon(new ImageIcon(ICONS.class.getResource("stop.png")));
		stopButton.setEnabled(false);
		stopButton.addActionListener(e -> {
			if (thread != null) {
				thread.interrupt();
			}
		});
		toolaBar.add(stopButton);

		// Spinner Steps
		toolaBar.add(new JLabel(" Steps: "));
		stepsSpinner = new JSpinner(new SpinnerNumberModel(10000, 1, 10000, 100));
		stepsSpinner.setToolTipText("Simulation steps to run: 1-10000");
		stepsSpinner.setMaximumSize(new Dimension(80, 40));
		stepsSpinner.setMinimumSize(new Dimension(80, 40));
		stepsSpinner.setPreferredSize(new Dimension(80, 40));
		toolaBar.add(stepsSpinner);

		// Text Delta-Time
		toolaBar.add(new JLabel(" Delta-Time: "));
		deltaText = new JTextField(5);
		deltaText.setToolTipText("Delta-Time");
		deltaText.setText("" + Main.deltaTime);
		deltaText.setMaximumSize(new Dimension(80, 40));
		deltaText.setMinimumSize(new Dimension(80, 40));
		deltaText.setPreferredSize(new Dimension(80, 40));
		toolaBar.add(deltaText);

		// Spinner Delay
		toolaBar.add(new JLabel(" Delay: "));
		delaySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
		delaySpinner.setToolTipText("Delay entre steps (ms)");
		delaySpinner.setMaximumSize(new Dimension(80, 40));
		delaySpinner.setMinimumSize(new Dimension(80, 40));
		delaySpinner.setPreferredSize(new Dimension(80, 40));
		toolaBar.add(delaySpinner);

		// Quit Button
		toolaBar.add(Box.createGlue());
		toolaBar.addSeparator();
		quitButton = new JButton();
		quitButton.setToolTipText("Exit the application");
		quitButton.setIcon(new ImageIcon(ICONS.class.getResource("exit.png")));
		quitButton.addActionListener(e -> ViewUtils.quit(this));
		toolaBar.add(quitButton);
	
		// Inicialización de componentes auxiliares
		this.fc = new JFileChooser();
		this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		this.changeRegionsDialog = new ChangeRegionsDialog(ctrl);
		
	}


	private void runSim(int n, double dt, int delay) {
		while (n > 0 && !Thread.currentThread().isInterrupted()) {
			try {
				ctrl.advance(dt);
				if (delay > 0) {
					Thread.sleep(delay);
				}
				n--;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			} catch (Exception e) {
				SwingUtilities.invokeLater(() -> ViewUtils.showErrorMsg("Execution error: " + e.getMessage()));
				return;
			}
		}
	}

	private void setEnableButtons(boolean enable) {
		openButton.setEnabled(enable);
		viewerButton.setEnabled(enable);
		regionsButton.setEnabled(enable);
		runButton.setEnabled(enable);
		stepsSpinner.setEnabled(enable);
		deltaText.setEnabled(enable);
		delaySpinner.setEnabled(enable);
		quitButton.setEnabled(enable);

		stopButton.setEnabled(!enable);
	}
}