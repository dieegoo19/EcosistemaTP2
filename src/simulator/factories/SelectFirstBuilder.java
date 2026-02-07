package simulator.factories;

import org.json.JSONObject;

import simulator.model.selection.SelectFirst;

public class SelectFirstBuilder extends Builder<SelectFirst>{

	public SelectFirstBuilder() {
		super("","");
	}

	@Override
	protected SelectFirst createInstance(JSONObject data) {
		return new SelectFirst();
	}

}
