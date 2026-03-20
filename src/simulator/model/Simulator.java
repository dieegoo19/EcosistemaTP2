package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;
import simulator.factories.Factory;


public class Simulator implements JSONable, Observable<EcoSysObserver> {

	private Factory<Animal> aF;
	private Factory<Region> rF;
	private RegionManager regionMngr;
	private List<Animal> animals; // Al meterlo como parametro en los observers hacer copia (Array<Animal> != Array<AnimalInfo>)
	private double t;
	
	private List<EcoSysObserver> observers = new ArrayList<>();;
	
	
	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
		regionMngr = new RegionManager(cols, rows, width, height);
		animals = new ArrayList<>();
		aF = animalsFactory;
		rF = regionsFactory;
		t = 0.0;
	}
	
	
	private void setRegion(int row, int col, Region r) {
		this.regionMngr.setRegion(row, col, r);
	}
	
	public void setRegion(int row, int col, JSONObject rJson) {
		Region r = rF.createInstance(rJson);
		setRegion(row, col, r);

		notifyOnRegionSet(row, col, r);
	}
	
	private void addAnimal(Animal a) {
		this.animals.add(a);
		this.regionMngr.registerAnimal(a);
	}
	
	public void addAnimal(JSONObject aJson) {
		Animal a = aF.createInstance(aJson);
		addAnimal(a);
		
		notifyOnAnimalAdded(a);
	}
	
	public MapInfo getMapInfo() {
		return regionMngr;
	}
	
	public List<? extends AnimalInfo> getAnimals(){
		return Collections.unmodifiableList(animals);
	}
	
	public double getTime() {
		return t;
	}
	
	private boolean deadAnimalAction(Animal a) {
		if (a.getState() == State.DEAD) {
            regionMngr.unregisterAnimal(a);
            return true;
        }
        return false;
	}

	public void advance(double dt) {
	    t += dt;
		animals.removeIf(a -> deadAnimalAction(a));
		for(Animal a: animals) {a.update(dt); regionMngr.updateAnimalRegion(a);}
		this.regionMngr.updateAllRegions(dt);
		List<Animal> babies = new ArrayList<>();
		for(Animal a: animals) {if(a.isPregnant()) babies.add(a.deliverBaby());}
		for(Animal a: babies) {addAnimal(a);}
	    
	    notifyOnAdvance(dt);
	}
	
	public JSONObject asJSON() {
		JSONObject jo1 = new JSONObject();
        jo1.put("time", t);
        jo1.put("state", regionMngr.asJSON());
        return jo1;
	}

	public void reset(int cols, int rows, int width, int height) {
		animals = new ArrayList<>();
		regionMngr = new RegionManager(cols, rows, width, height);
		t = 0.0;
		
		notifyOnReset();
	}
	
	// ----------------------------------------------------------
	// OBSERVERS
	// ----------------------------------------------------------
	@Override
	public void addObserver(EcoSysObserver o) {
	    if (o != null && !observers.contains(o)) {
	        observers.add(o);
	        o.onRegister(t, regionMngr, new ArrayList<>(this.animals));
	    }
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
	    observers.remove(o);
	}

	// ----------------------------------------------------------
	// NOTIFICATIONS
	// ----------------------------------------------------------
	private void notifyOnReset() {
	    List<AnimalInfo> animalsInfo = new ArrayList<>(this.animals);
	    for (EcoSysObserver o : observers) {
	        o.onReset(t, regionMngr, animalsInfo);
	    }
	}

	private void notifyOnAdvance(double dt) {
	    List<AnimalInfo> animalsInfo = new ArrayList<>(this.animals);
	    for (EcoSysObserver o : observers) {
	        o.onAdvance(t, regionMngr, animalsInfo, dt);
	    }
	}

	private void notifyOnAnimalAdded(Animal a) {
	    List<AnimalInfo> animalsInfo = new ArrayList<>(this.animals);
	    for (EcoSysObserver o : observers) {
	        o.onAnimalAdded(t, regionMngr, animalsInfo, a);
	    }
	}

	private void notifyOnRegionSet(int row, int col, RegionInfo r) {
	    for (EcoSysObserver o : observers) {
	        o.onRegionSet(row, col, regionMngr, r);
	    }
	}
}
