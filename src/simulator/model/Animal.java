package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {
	private String geneticCode;
	private Diet diet;
	private State state;
	private Vector2D pos;
	private Vector2D dest;
	private double energy;
	private double speed;
	private double age;
	private double desire;
	private double sightRange;
	private Animal mateTarget;
	private Animal baby;
	private AnimalMapView regionMngr;
	private SelectionStrategy mateStrategy;
	
	protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy, Vector2D pos) throws IllegalArgumentException{
		if(geneticCode == null || geneticCode == "" || sightRange < 0 || initSpeed < 0 || mateStrategy == null)
			throw new IllegalArgumentException("Argumentos no validos");
		this.geneticCode = geneticCode;
		this.diet = diet;
		this.sightRange = sightRange;
		this.speed = Utils.getRandomizedParameter(initSpeed, 0.1);
		this.mateStrategy = mateStrategy;
		this.pos = pos;
		this.state = State.NORMAL;
		this.energy = 100.00;
		this.desire = 0.0;
		this.dest = null;
		this.mateTarget = null;
		this.baby = null;
		this.regionMngr = null;
	}
	
	protected Animal(Animal p1, Animal p2) {
		this.age = 0;
		this.geneticCode = p1.getGeneticCode();
		this.diet = p1.getDiet();
		this.sightRange = Utils.getRandomizedParameter((p1.getSightRange()+p2.getSightRange())/2, 0.2);
		this.speed = Utils.getRandomizedParameter((p1.getSpeed()+p2.getSpeed())/2, 0.2);
		this.mateStrategy = p2.mateStrategy;
		this.pos = p1.getPosition().plus(Vector2D.getRandomVector(-1,1).scale(60.0*(Utils.RAND.nextGaussian()+1)));
		this.state = State.NORMAL;
		this.energy = (p1.getEnergy() + p2.getEnergy()) / 2.00;
		this.desire = 0.0;
		this.dest = null;
		this.mateTarget = null;
		this.baby = null;
		this.regionMngr = null;
	}
	
	public void init(AnimalMapView regMngr) {
		double width = 0, height = 0;
		this.regionMngr = regMngr;
		if(this.pos == null) {
			//TODO: cambiar a lo de regMngr
			this.pos = Vector2D.randomInMapVector(width, height);
		}
		else this.pos.mapAdjustment(width, height);
		this.dest = Vector2D.randomInMapVector(width, height);
	}
	
	public Animal deliverBaby() {
		Animal aux = this.baby;
		this.baby = null;
		return aux;
	}
	
	protected void move(double speed) {
		pos = pos.plus(dest.minus(pos).direction().scale(speed));
	}
	
	protected void setState(State state) {
		this.state = state;
		switch (state) {
		case NORMAL:
			setNormalStateAction();
			break;
		case MATE:
			setMateStateAction();
			break;
		case HUNGER:
			setHungerStateAction();
			break;
		case DANGER:
			setDangerStateAction();
			break;
		case DEAD:
			setDeadStateAction();
			break;
		}
	}
	
	protected abstract void setNormalStateAction();
	protected abstract void setDangerStateAction();
	protected abstract void setMateStateAction();
	protected abstract void setHungerStateAction();
	protected abstract void setDeadStateAction();
	
	
	public JSONObject asJSON() {
		
		JSONObject jo1 = new JSONObject();

		jo1.put("pos", pos.vectorToPair());
		jo1.put("gcode", geneticCode);
		jo1.put("diet", diet);
		jo1.put("state", state);
		
		return jo1;
	}
	
	
	@Override
	public String getGeneticCode() {
		return geneticCode;
	}
	
	@Override
	public Diet getDiet() {
		return diet;
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public Vector2D getPosition() {
		return pos;
	}

	public Vector2D getDestination() {
		return dest;
	}

	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	@Override
	public double getAge() {
		return age;
	}

	protected double getDesire() {
		return desire;
	}

	@Override
	public double getSightRange() {
		return sightRange;
	}

	@Override
    public boolean isPregnant() {
        return baby != null;
    }
	
	
}
