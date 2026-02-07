package simulator.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import simulator.factories.Factory;
import simulator.model.elements.Animal;
import simulator.model.region.Region;
import simulator.model.region.RegionManager;

public class Simulator implements JSONable{
	
	private int cols;
	private int rows;
	private int width;
	private int height;
	private Factory<Animal> anFact;
	private Factory<Region> regFact;
	private RegionManager regMngr;
	private Set<Animal> animalsList;
	private double time;
	
	public Simulator(int cols, int rows, int width, int height,   
            Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
		this.cols = cols;
		this.rows = rows;
		this.width = width;
		this.height = height;
		this.anFact = animalsFactory;
		this.regFact = regionsFactory;
		this.regMngr = new RegionManager(cols,rows,width,height);
		this.time = 0.0;
		this.animalsList = new HashSet<>();
	}
	
	

}
