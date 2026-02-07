package simulator.model.region;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.*;
import simulator.model.elements.Animal;
import simulator.model.elements.AnimalInfo;

public class RegionManager implements AnimalMapView{
	
	private final int cols;
	private final int rows;
	private final int regionWidth;
	private final int regionHeight;
	private final int width;
	private final int height;
	
	private Region[][] regions;
	private Map<Animal, Region> animalRegion;
	

	public RegionManager(int cols, int rows, int width, int height) {
		this.cols = cols;
		this.rows = rows;
		
		this.width = width;
		this.height = height;
		
		this.regionWidth = width/cols;
		this.regionHeight = height/rows;
		
		this.animalRegion = new HashMap<>();
		
		this.regions = new Region[rows][cols];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				this.regions[i][j] = new DefaultRegion();
			}
		}
	}

	@Override
	public int getCols() {
		return cols;
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getRegionWidth() {
		return regionWidth;
	}

	@Override
	public int getRegionHeight() {
		return regionHeight;
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		return this.animalRegion.get(a).getFood(a, dt);
	}
	
	public void updateAllRegions(double dt) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				this.regions[i][j].update(dt);
			}
		}
	}

	@Override
	public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter) {
	    Set<Region> regionesAnexas = new HashSet<>();
	    double range = e.getSightRange();
	    
	    int maxCol = Math.min(regions.length - 1, (int) ((e.getPosition().getX() + range) / regionWidth));
	    int minCol = Math.max(0, (int) ((e.getPosition().getX() - range) / regionWidth));
	    int maxRow = Math.min(regions[0].length - 1, (int) ((e.getPosition().getY() + range) / regionHeight));
	    int minRow = Math.max(0, (int) ((e.getPosition().getY() - range) / regionHeight));
	    
	    for(int i = minRow; i <= maxRow; i++) {
	    	for(int j = minCol; j <= maxCol; j++) {
	    		regionesAnexas.add(regions[i][j]);
	    	}
	    }
	    
	    return regionesAnexas.stream()
	    		// FLATMAP: "Para cada región, dame su Stream de animales y júntalos todos"
	            .flatMap(region -> region.getAnimals().stream()) 
	            // FILTER: "Para cada animal que pase por aquí, aplica esta condición"
	            .filter(filter)
	            // distinct() asegura que no haya duplicados en el resultado
	            .distinct() 
	            // COLLECT: "Toma todo lo que sobrevivió y mételo en una lista"
	            .collect(Collectors.toList());
	}
	
	private void addAnimal(Region r, Animal a) {
		r.addAnimal(a);
		this.animalRegion.put(a, r);
	}
	
	private void removeAnimal(Region r, Animal a) {
		r.removeAnimal(a);
		this.animalRegion.remove(a);
	}
	
	public void setRegion(int row, int col, Region r) {
		List<Animal> aux = this.regions[row][col].getAnimals();
		this.regions[row][col] = r;
		for(Animal an: aux) {
			addAnimal(this.regions[row][col], an);
		}
	}
	
	private void registerAnimal(Animal a) {
		int row = (int)(a.getPosition().getY()/this.regionHeight);
		int col = (int)(a.getPosition().getX()/this.regionWidth);
		addAnimal(this.regions[row][col], a);
		a.init(this);
	}
	
	public void unregisterAnimal(Animal a) {
		Region r = this.animalRegion.get(a);
		removeAnimal(r, a);
	}
	
	public void updateAnimalRegion(Animal a) {
		int row = (int)(a.getPosition().getY()/this.regionHeight);
		int col = (int)(a.getPosition().getX()/this.regionWidth);
		Region regAct = this.animalRegion.get(a);
		Region realReg = this.regions[row][col];
		if(!realReg.equals(regAct)){
			addAnimal(realReg, a);
			removeAnimal(regAct, a);
		}
	}
	
	public JSONObject asJSON() {
		JSONArray arr = new JSONArray();
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				JSONObject aux = new JSONObject();
				aux.put("row", i);
				aux.put("col", j);
				aux.put("data", this.regions[i][j].asJSON());
				arr.put(aux);
			}
		}
		return new JSONObject().put("regions", arr);
	}

}
