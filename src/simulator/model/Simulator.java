package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.model.elements.Animal;
import simulator.model.elements.AnimalInfo;
import simulator.model.region.Region;
import simulator.model.region.RegionManager;

public class Simulator implements JSONable{
	
	private Factory<Animal> anFact;
	private Factory<Region> regFact;
	private RegionManager regMngr;
	private Set<Animal> animalsList;
	private double time;
	
	public Simulator(int cols, int rows, int width, int height,   
            Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
		this.anFact = animalsFactory;
		this.regFact = regionsFactory;
		this.regMngr = new RegionManager(cols,rows,width,height);
		this.time = 0.0;
		this.animalsList = new HashSet<>();
	}
	
	private void setRegion(int row, int col, Region r) {
		this.regMngr.setRegion(row, col, r);
	}
	
	public void setRegion(int row, int col, JSONObject rJson) {
		setRegion(row, col, this.regFact.createInstance(rJson));
	}
	
	private void addAnimal(Animal a) {
		this.animalsList.add(a);
		this.regMngr.registerAnimal(a);
	}
	
	public void addAnimal(JSONObject aJson) {
		addAnimal(this.anFact.createInstance(aJson));
	}
	
	public MapInfo getMapInfo() {
		return this.regMngr;
	}
	
	public List<? extends AnimalInfo> getAnimals(){
		return Collections.unmodifiableList(this.animalsList.stream().toList());
	}
	
	public double getTime() {
		return this.time;
	}
	
	private boolean deadAnimalAction(Animal a) {
		if (a.getState() == State.DEAD) {
            regMngr.unregisterAnimal(a);
            return true;
        }
        return false;
	}
	
	public void advance(double dt) {
		time += dt;
		animalsList.removeIf(a -> deadAnimalAction(a));
		for(Animal a: animalsList) {a.update(dt); regMngr.updateAnimalRegion(a);}
		this.regMngr.updateAllRegions(dt);
		List<Animal> babies = new ArrayList<>();
		for(Animal a: animalsList) {if(a.isPregnant()) babies.add(a.deliverBaby());}
		for(Animal a: babies) {addAnimal(a);}
	}
	
	public JSONObject asJSON() {
		JSONObject obj = new JSONObject();
		obj.put("time", time);
		obj.put("state", regMngr.asJSON());
		return obj;
	}
}
