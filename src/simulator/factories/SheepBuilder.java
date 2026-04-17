package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.*;

public class SheepBuilder extends AnimalBuilder {

	public SheepBuilder(Factory<SelectionStrategy> selectionStrategyFactory) throws IllegalArgumentException {
		super("sheep", "Crea una oveja.", selectionStrategyFactory);
	}

	@Override
	protected Sheep createInstance(JSONObject data) {
		SelectionStrategy mateStr = super.get_strategy(data, "mate_strategy");
		SelectionStrategy dangerStr = super.get_strategy(data, "danger_strategy");
		Vector2D pos = super.get_pos(data);

		return new Sheep(mateStr, dangerStr, pos);
	}

	@Override
	protected void fillInData(JSONObject o) {
	}
}
