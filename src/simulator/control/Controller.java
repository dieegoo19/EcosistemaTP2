package simulator.control;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.Simulator;

public class Controller {
	
	Simulator sim;
	
	public Controller(Simulator sim) {
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
	
	//TODO: DUDA CON EL OUT
	public void run(double t, double dt, boolean sv, OutputStream out) {
		JSONObject obj = new JSONObject();
		obj.put("in", sim.asJSON());
		while(sim.getTime() <= t) {
			sim.advance(dt);
		}
		obj.put("out", sim.asJSON());

		String jsonString = obj.toString();
		byte[] binario = jsonString.getBytes(StandardCharsets.UTF_8);
		
		try {
			out.write(binario);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//TODO: FALTA LO DEL VISOR
	}
}
