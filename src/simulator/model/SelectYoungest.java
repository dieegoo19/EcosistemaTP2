package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy {

	public SelectYoungest() {
	}

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty())
			return null;
		Animal ret = as.getFirst();
		double minAge = as.getFirst().getAge();
		for (Animal an : as) {
			if (minAge > an.getAge()) {
				ret = an;
				minAge = an.getAge();
			}
		}
		return ret;
	}

}
