package simulator.model.selection;

import java.util.List;

import simulator.model.elements.Animal;

public interface SelectionStrategy {  
	Animal select(Animal a, List<Animal> as);  
}