package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.*;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller {
	
	Simulator sim;
	
	public Controller(Simulator sim) {
		if (sim == null) {
            throw new IllegalArgumentException("Simulator cannot be null");
        }
        this.sim = sim;
	}
	
	
	private void treatRegJSON(JSONObject obj) {
		JSONArray row = obj.getJSONArray("row");
		int rf = row.getInt(0);
		int rt = row.getInt(1);
		
		JSONArray col = obj.getJSONArray("col");
		int cf = col.getInt(0);
		int ct = col.getInt(1);
		
		JSONObject spec = obj.getJSONObject("spec");
		
		for(int i = rf; i <= rt; i++) {
			for(int j = cf; j <= ct; j++) {
				sim.setRegion(i, j, spec);
			}
		}
		
	}
	
	private void treatAnimalsJSON(JSONObject obj) {
		int N = obj.getInt("amount");
		JSONObject spec = obj.getJSONObject("spec");
		for(int i = 0; i < N; i++) {
			sim.addAnimal(spec);
		}
	}
	
	public void loadData(JSONObject data) {
		if(data.has("regions")) {
			JSONArray regs = data.getJSONArray("regions");
			regs.forEach(item -> treatRegJSON((JSONObject)item));
		}
		JSONArray animals = data.getJSONArray("animals");
		animals.forEach(item -> treatAnimalsJSON((JSONObject)item));
	}
	
	private List<ObjInfo> toAnimalsInfo(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(
					a.getGeneticCode(), 
					(int) a.getPosition().getX(), 
					(int) a.getPosition().getY(),
					(int) Math.round(a.getAge()) + 2
			));
		return ol;
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) {
		SimpleObjectViewer view = null;  
		if (sv) {  
		   MapInfo m = sim.getMapInfo();  
		   view = new SimpleObjectViewer("[ECOSYSTEM]", m.getWidth(), m.getHeight(), m.getCols(), m.getRows());  
		   view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);  
		}
		
		JSONObject obj = new JSONObject();
		obj.put("in", sim.asJSON());
		while(sim.getTime() <= t) { 
			sim.advance(dt); 
			if (sv) view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
		}
		obj.put("out", sim.asJSON());

		PrintStream p = new PrintStream(out);
		p.println(obj.toString(2));
		
		if (sv) view.close();
	}
	
	public void reset(int cols, int rows, int width, int height) {
		sim.reset(cols, rows, width, height);
	}
	
	public void setRegions(JSONObject rs) {
		fillRegions(rs);
	}
	
	public void advance(double dt) {
		sim.advance(dt);
	}
	
	public void addObserver(EcoSysObserver o) {
		sim.addObserver(o);
	}
	
	public void removeObserver(EcoSysObserver o) {
		sim.removeObserver(o);
	}
	
	// TODO REVISAR
	private void fillRegions(JSONObject rs) {
		if (rs.has("regions")) {
			JSONArray regions = rs.getJSONArray("regions");
			for(int i = 0; i < regions.length(); i++) {
				JSONObject r = regions.getJSONObject(i);
				JSONArray rowRange = r.getJSONArray("row");
				JSONArray colRange = r.getJSONArray("col");
				JSONObject spec = r.getJSONObject("spec");
			
				for(int row = rowRange.getInt(0); row <= rowRange.getInt(1); row++) {
					for(int col = colRange.getInt(0); col <= colRange.getInt(1); col++) {
						sim.setRegion(row, col, spec);
					}
				}
			}
		}
	}
}
