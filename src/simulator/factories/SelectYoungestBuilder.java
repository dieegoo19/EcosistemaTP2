package simulator.factories;

import org.json.JSONObject;

import simulator.model.*;

public class SelectYoungestBuilder extends Builder<SelectionStrategy> {

	public SelectYoungestBuilder() throws IllegalArgumentException {
		super("youngest", "Selecciona el animal mas joven de la lista.");
	}

	@Override
	protected SelectYoungest createInstance(JSONObject data) {
		return new SelectYoungest();
	}
}
