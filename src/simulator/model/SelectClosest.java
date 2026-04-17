package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {

	public SelectClosest() {
	}

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty())
			return null;
		Animal aux = as.get(0);
		for (Animal an : as) {
			if (!a.equals(an)
					&& a.getPosition().distanceTo(an.getPosition()) < a.getPosition().distanceTo(aux.getPosition()))
				aux = an;
		}
		return aux;
	}

}
