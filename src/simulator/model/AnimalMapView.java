package simulator.model;

import java.util.List;
import java.util.function.Predicate;

import simulator.model.elements.Animal;

public interface AnimalMapView extends MapInfo, FoodSupplier {  
	public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter);  
}
