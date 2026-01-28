package simulator.model.selection;

import java.util.List;

import simulator.model.elements.Animal;

public class SelectYoungest implements SelectionStrategy{

	public SelectYoungest() {}

	@Override
	public Animal select(Animal a, List<Animal> as) {
		Animal ret = as.getFirst();
		double minAge = as.getFirst().getAge();
		for(Animal an: as) {
			if(minAge > an.getAge()) { 
				ret = an;
				minAge = an.getAge();
			}
		}
		return ret;
	}

}
