package simulator.view;

import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

	// Anchura/altura de la simulación -- se supone que siempre van a ser
	// iguales al tamaño del componente

	private int width;
	private int height;

	// Número de filas/columnas de la simulación (regiones)

	private int rows;
	private int cols;

	// Anchura/altura de una región

	int rWidth;
	int rHeight;

	// Mostramos sólo animales con este estado. Los posibles valores de currState
	// son null, y los valores de Animal.State.values(). Si es null mostramos todo.

	Animal.State currState;

	// En estos atributos guardamos la lista de animales y el tiempo que hemos
	// recibido la última vez para dibujarlos.

	volatile private Collection<AnimalInfo> objs;
	volatile private Double time;

	// Una clase auxilar para almacenar información sobre una especie.

	private static class SpeciesInfo {
		private Integer count;
		private Color color;

		SpeciesInfo(Color color) {
			count = 0;
			this.color = color;
		}
	}

	// Un mapa para la información sobre las especies.

	Map<String, SpeciesInfo> kindsInfo = new HashMap<>();

	// El font que usamos para dibujar texto.

	private Font textFont = new Font("Arial", Font.BOLD, 12);

	// Indica si mostramos el texto la ayuda o no.

	private boolean showHelp;

	public MapViewer() {
		initGUI();
	}

	private void initGUI() {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 'h':
					showHelp = !showHelp;
					repaint();
					break;
				case 's':
					if (currState == null) {
						currState = Animal.State.values()[0];
					} else {
						int nextIdx = currState.ordinal() + 1;
						if (nextIdx < Animal.State.values().length) {
							currState = Animal.State.values()[nextIdx];
						} else {
							currState = null;
						}
					}
					repaint();
					break;
				default:
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				// Esto es necesario para capturar las teclas cuando el ratón está sobre este
				// componente.
				requestFocus();
			}
		});

		// Por defecto mostramos todos los animales.
		currState = null;

		// Por defecto mostramos el texto de ayuda.
		showHelp = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Cambiar el font para dibujar texto.
		g.setFont(textFont);

		// Dibujar fondo blanco.
		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, width, height);

		// Dibujar los animales, el tiempo, informacion sobre los especie, etc.
		if (objs != null)
			drawObjects(gr, objs, time);

		// Mostrar el texto de ayuda si showHelp es true. El texto a mostrar es el
		// siguiente (en 2 líneas):
		// h: toggle help
		// s: show animals of a specific state
		if (this.showHelp) {
			g.setColor(Color.RED);
			g.drawString("h: toggle help", 10, 20);
			g.drawString("s: show animals of a specific state", 10, 35);
		}
	}

	private boolean visible(AnimalInfo a) {
		// Devolver true si el animal es visible, es decir si currState es null o
		// su estado es igual a currState.
		return this.currState == null || a.getState() == this.currState;
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {
		// Dibujar el grid de regiones.
		g.setColor(Color.LIGHT_GRAY);
		for (int i = 0; i <= cols; i++)
			g.drawLine(i * rWidth, 0, i * rWidth, height);
		for (int j = 0; j <= rows; j++)
			g.drawLine(0, j * rHeight, width, j * rHeight);

		// Dibujar los animales.
		for (AnimalInfo a : animals) {

			// Si no es visible saltamos la iteración.
			if (!visible(a))
				continue;

			// La información sobre la especie de 'a'.
			SpeciesInfo speciesInfo = kindsInfo.get(a.getGeneticCode());

			// Si espInfo es null, añade una entrada correspondiente al mapa. Para el
			// color usa ViewUtils.getColor(a.getGeneticCode()).
			if (speciesInfo == null) {
				speciesInfo = new SpeciesInfo(ViewUtils.getColor(a.getGeneticCode()));
				kindsInfo.put(a.getGeneticCode(), speciesInfo);
			}

			// Incrementar el contador de la especie (es decir el contador dentro de
			// speciesInfo).
			speciesInfo.count++;

			// Dibujar el animal en la posicion correspondiente, usando el color
			// speciesInfo.color. Su tamaño tiene que ser relativo a su edad, por ejemplo
			// edad/2+2. Se puede dibujar usando fillRoundRect, fillRect o fillOval.
			g.setColor(speciesInfo.color);
			int size = (int) Math.round(a.getAge()) + 2;
			g.fillRoundRect((int) a.getPosition().getX(), (int) a.getPosition().getY(), size, size, 3, 3);
		}

		// Dibujar la etiqueta del estado visible, usando currState.toString(), si no
		// es null.
		int yOffset = height - 20;

		// Dibujar la etiqueta del tiempo. Para escribir solo 3 decimales puede
		// usar String.format("%.3f", time).
		g.setColor(Color.MAGENTA);
		drawStringWithRect(g, 20, yOffset, String.format("Time: %.3f", time));
		yOffset -= 20;

		// Dibujar la información de todas la especies. Al final de la iteración
		// poner el contador de la especie correspondiente a 0 (para resetear el cuento)
		for (Entry<String, SpeciesInfo> e : kindsInfo.entrySet()) {
			g.setColor(e.getValue().color);
			drawStringWithRect(g, 20, yOffset, e.getKey() + ": " + e.getValue().count);
			e.getValue().count = 0;
			yOffset -= 20;
		}

		// Estado visible
		if (currState != null) {
			g.setColor(Color.BLUE);
			drawStringWithRect(g, 20, yOffset, "State: " + currState.toString());
		}
	}

	// Un método que dibujar un texto con un rectángulo.
	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		// Almacenar objs y time en los atributos correspondientes y llamar a
		// repaint() para redibujar el componente.
		this.objs = objs;
		this.time = time;
		repaint();
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		// Actualizar los atributos width, height, cols, rows, etc.
		this.width = map.getWidth();
		this.height = map.getHeight();
		this.rows = map.getRows();
		this.cols = map.getCols();

		this.rWidth = width / cols;
		this.rHeight = height / rows;
		// Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
		// porque en MapWindow llamamos a pack() después de llamar a reset.
		setPreferredSize(new Dimension(map.getWidth(), map.getHeight()));

		// Dibuja el estado.
		update(animals, time);
	}

}
