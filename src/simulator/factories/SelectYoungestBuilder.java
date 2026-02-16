package simulator.factories;

import org.json.JSONObject;

import simulator.model.selection.SelectClosest;
import simulator.model.selection.SelectFirst;
import simulator.model.selection.SelectYoungest;
import simulator.model.selection.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy>{

	public SelectYoungestBuilder()  throws IllegalArgumentException{
		super("youngest","Selecciona el animal mas joven de la lista.");
	}

	@Override
	protected SelectYoungest createInstance(JSONObject data) {
		return new SelectYoungest();
	}
}
