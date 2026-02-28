package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

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
	
	protected int getAnimalsCountByDiet(Diet diet) {
		return (int) getAnimals().stream()
				.filter(a -> a.getDiet() == diet && a.getState() != State.DEAD)
				.count();
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
