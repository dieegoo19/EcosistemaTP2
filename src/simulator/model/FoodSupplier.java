package simulator.model;

import simulator.model.elements.AnimalInfo;

public interface FoodSupplier {  
	double getFood(AnimalInfo a, double dt);  
}