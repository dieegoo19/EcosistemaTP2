package simulator.model.selection;

import java.util.List;

import simulator.model.elements.Animal;

public class SelectClosest implements SelectionStrategy{

	public SelectClosest() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Animal select(Animal a, List<Animal> as) {
		Animal aux = as.get(0);
		for(Animal an: as) {
			if(!a.equals(an) && a.getPosition().distanceTo(an.getPosition()) < a.getPosition().distanceTo(aux.getPosition())) 
				aux = an;
		}
		return aux;
	}

}
