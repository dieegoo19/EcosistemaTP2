package simulator.model.selection;

import java.util.List;

import simulator.model.elements.Animal;

public class SelectFirst implements SelectionStrategy{

	public SelectFirst() {}

	@Override
	public Animal select(Animal a, List<Animal> as) {
		return as.get(0);
	}

}
