package simulator.model.region;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import simulator.model.*;
import simulator.model.elements.Animal;
import simulator.model.elements.AnimalInfo;

public class RegionManager implements AnimalMapView{
	
	private int cols;
	private int rows;
	private int regionWidth;
	private int regionHeight;
	private int width;
	private int height;
	
	private Region[][] regions;
	Map<Animal, Region> animalRegion;
	

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
		return 0;
	}

	@Override
	public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter) {
		return null;
	}
	
	private void addAnimal(Region r, Animal a) {
		r.addAnimal(a);
		this.animalRegion.put(a, r);
	}
	
	private void removeAnimal(Region r, Animal a) {
		r.removeAnimal(a);
		this.animalRegion.remove(a);
	}
	
	private void setRegion(int row, int col, Region r) {
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
	
	private void unregisterAnimal(Animal a) {
		Region r = this.animalRegion.get(a);
		removeAnimal(r, a);
	}
	
	private void updateAnimalRegion(Animal a) {
		//TODO: REVISAR
		int row = (int)(a.getPosition().getY()/this.regionHeight);
		int col = (int)(a.getPosition().getX()/this.regionWidth);
		Region aux = this.animalRegion.get(a);
		if(!this.regions[row][col].equals(this.animalRegion.get(a))){
			addAnimal(this.regions[row][col], a);
			removeAnimal(aux, a);
		}
		
	}

}
