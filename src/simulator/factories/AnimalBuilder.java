package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.elements.Animal;
import simulator.model.selection.SelectFirst;
import simulator.model.selection.SelectionStrategy;

public abstract class AnimalBuilder extends Builder<Animal> {
    protected Factory<SelectionStrategy> selectionStrategyFactory;

    public AnimalBuilder(String typeTag, String desc, Factory<SelectionStrategy> selectionStrategyFactory) {
        super(typeTag, desc);
        this.selectionStrategyFactory = selectionStrategyFactory;
    }

    protected Vector2D get_pos(JSONObject data) {
        if (data.has("pos")) {
            JSONObject posData = data.getJSONObject("pos");
            JSONArray xRange = posData.getJSONArray("x_range");
            JSONArray yRange = posData.getJSONArray("y_range");
            
            double min = xRange.getDouble(0);
            double max = xRange.getDouble(1);
            double x = min + Utils.RAND.nextDouble() * (max - min);
            min = yRange.getDouble(0);
            max = yRange.getDouble(1);
            double y = min + Utils.RAND.nextDouble() * (max - min);
            
            return new Vector2D(x, y);
        }
        return null;
    }

    protected SelectionStrategy get_strategy(JSONObject data, String key) {
        return data.has(key) ? selectionStrategyFactory.createInstance(data.getJSONObject(key)) : new SelectFirst();
    }
    
    @Override
    protected void fillInData(JSONObject o) {
        JSONObject pos = new JSONObject();
        pos.put("x_range", "[min, max]");
        pos.put("y_range", "[min, max]");
        o.put("pos", pos);
    }
}