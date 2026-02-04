package simulator.model.region;

import simulator.model.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.elements.Animal;

public abstract class Region implements Entity, FoodSupplier, RegionInfo{
	
	private List<Animal> animalsList;
	
	public Region() {
		this.animalsList = new ArrayList<>();
	}
	
	final void addAnimal(Animal a) {
		this.animalsList.add(a);
	}
	
	final void removeAnimal(Animal a) {
		this.animalsList.remove(a);
	} 
	
	final List<Animal> getAnimals(){
        return Collections.unmodifiableList(animalsList);
	}
	
	protected int getHerbivoreAnimalsCount() {
		return (int) getAnimals().stream()
				.filter(a -> a.getDiet() == Diet.HERBIVORE)
				.count();
		//TODO: .collect(Collectors.toList()).size();
	}
	
	public JSONObject asJSON() {
		JSONObject ret = new JSONObject();
		JSONArray arr = new JSONArray();
		for(Animal an: animalsList) {
			arr.put(an.asJSON());
		}
		ret.put("animals", arr);
		return ret;
	}

}
