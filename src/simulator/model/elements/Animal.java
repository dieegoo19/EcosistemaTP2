package simulator.model.elements;

import simulator.model.selection.*;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.AnimalMapView;
import simulator.model.Diet;
import simulator.model.Entity;
import simulator.model.State;

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
	protected AnimalMapView regionMngr;
	private SelectionStrategy mateStrategy;
	protected final double foodDropRate;
	protected final double desireIncreaseRate;
	protected final double boostFactor;
	protected final double maxAge;
	protected final double foodDropBoostFactor;
	
	protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy, Vector2D pos, 
			double foodDropRate, double desireIncreaseRate, double boostFactor, double maxAge, double foodDropBoostFactor) throws IllegalArgumentException{
		if(geneticCode == null || geneticCode.isEmpty() || sightRange < 0 || initSpeed < 0 || mateStrategy == null)
			throw new IllegalArgumentException("Argumentos no validos para crear un Animal");
		this.geneticCode = geneticCode;
		this.diet = diet;
		this.sightRange = sightRange;
		this.speed = Utils.getRandomizedParameter(initSpeed, 0.1);
		this.mateStrategy = mateStrategy;
		this.pos = pos;
		this.state = State.NORMAL;
		this.energy = 100.0;
		this.desire = 0.0;
		this.dest = null;
		this.mateTarget = null;
		this.baby = null;
		this.regionMngr = null;
		this.foodDropRate = foodDropRate;
		this.desireIncreaseRate = desireIncreaseRate; 
		this.boostFactor = boostFactor;
		this.maxAge = maxAge;
		this.foodDropBoostFactor = foodDropBoostFactor;
	}
	
	protected Animal(Animal p1, Animal p2) {
		this.age = 0.0;
		this.geneticCode = p1.getGeneticCode();
		this.diet = p1.getDiet();
		this.sightRange = Utils.getRandomizedParameter((p1.getSightRange()+p2.getSightRange())/2, 0.2);
		this.speed = Utils.getRandomizedParameter((p1.getSpeed()+p2.getSpeed())/2, 0.2);
		this.mateStrategy = p2.mateStrategy;
		this.pos = p1.getPosition().plus(Vector2D.getRandomVector(-1,1).scale(60.0*(Utils.RAND.nextGaussian()+1)));
		this.state = State.NORMAL;
		this.energy = (p1.getEnergy() + p2.getEnergy()) / 2.0;
		this.desire = 0.0;
		this.dest = null;
		this.mateTarget = null;
		this.baby = null;
		this.regionMngr = null;
		this.foodDropRate = p1.foodDropRate;
		this.desireIncreaseRate = p1.desireIncreaseRate;
		this.boostFactor = p1.boostFactor;
		this.maxAge = p1.maxAge;
		this.foodDropBoostFactor = p1.foodDropBoostFactor;
	}
	
	protected Animal(Animal other) {
        this.geneticCode = other.geneticCode;
        this.diet = other.diet;
        this.state = other.state;
        this.pos = other.pos;
        this.dest = other.dest;
        this.energy = other.energy;
        this.speed = other.speed;
        this.age = other.age;
        this.desire = other.desire;
        this.sightRange = other.sightRange;
        this.mateTarget = other.mateTarget;
        this.baby = other.baby;
        this.regionMngr = other.regionMngr;
        this.mateStrategy = other.mateStrategy;
        this.foodDropRate = other.foodDropRate;
		this.desireIncreaseRate = other.desireIncreaseRate;
		this.boostFactor = other.boostFactor;
		this.foodDropBoostFactor = other.foodDropBoostFactor;
		this.maxAge = other.maxAge;
    }
	
	protected abstract Animal copyOf();
	
	private Vector2D createRandomPosition() {
		if (regionMngr == null) return new Vector2D(0,0);
		double w = regionMngr.getWidth();
		double h = regionMngr.getHeight();
		return new Vector2D(Utils.RAND.nextDouble() * (w), Utils.RAND.nextDouble() * (h));
	}
	
	private void adjustPosition() {
		if (regionMngr != null) {
			double width = regionMngr.getWidth();
			double height = regionMngr.getHeight();
			double x = pos.getX();
			double y = pos.getY();
			
			while (x >= width) x = (x - width);  
			while (x < 0) x = (x + width);  
			while (y >= height) y = (y - height);  
			while (y < 0) y = (y + height);
			
			this.pos = new Vector2D(x, y);
		}
	}
	
	@Override
	public void update(double dt) {
		State st = this.getState();
		switch (st) {
		case NORMAL:
			NormalAction(dt);
			break;
		case MATE:
			MateAction(dt); 
			break;
		case HUNGER:
			HungerAction(dt);
			break;
		case DANGER:
			DangerAction(dt);
			break;
		case DEAD:
			DeadAction(dt);
			break;
		}
		
		double w = regionMngr.getWidth();
		double h = regionMngr.getHeight();
		double x = pos.getX();
		double y = pos.getY();
		
		if (x < 0 || x >= w || y < 0 || y >= h) {
			adjustPosition();
			setState(State.NORMAL);
		}
		
		if (energy <= 0.0 || age > maxAge) {
			setState(State.DEAD);
		}
		
		if (getState() != State.DEAD) {
			double food = regionMngr.getFood(this, dt);
			updateEnergy(energy + food);
		}
	}
	
	
	protected boolean inSightRange(Animal a) {
		if (a == null) return false;
		return this.getPosition().distanceTo(a.getPosition()) <= this.sightRange;
	}
	
	
	private void destinationCheck() {
		if (dest == null || getPosition().distanceTo(dest) < 8.0) {
			dest = createRandomPosition();
		}
	}
	
	// ----------------------------------
	// ACCIONES
	// ----------------------------------
	
	protected void firstActionNormal(double dt) {
		destinationCheck();
		move(speed * dt * Math.exp((energy - 100.0) * 0.007));
		updateAge(dt);
		updateEnergy(energy - foodDropRate * dt);
		updateDesire(desire + desireIncreaseRate * dt);
	}
	
	protected void NormalAction(double dt) {
		firstActionNormal(dt);
		setState(State.NORMAL);
	}
	
	protected abstract void DangerAction(double dt);
	protected abstract void HungerAction(double dt);
	
	protected void updateStats(double dt, double spFoodDropBoostFactor) {
		double s = boostFactor * speed * dt * Math.exp((energy - 100.0) * 0.007);
		move(s);
		updateAge(dt);
		updateEnergy(energy - foodDropRate * spFoodDropBoostFactor * dt);
		updateDesire(desire + desireIncreaseRate * dt);
	}
	
	protected void MateAction(double dt) {
		if (mateTarget != null && (mateTarget.getState() == State.DEAD || !inSightRange(mateTarget))) {
			mateTarget = null;
		}
		if (mateTarget == null) {
			List<Animal> candidates = regionMngr.getAnimalsInRange(this, a -> a.getGeneticCode().equals(this.geneticCode));
			mateTarget = mateStrategy.select(this, candidates);
			if (mateTarget == null) {
				firstActionNormal(dt);
			}
		} 
		if (mateTarget != null) {
			dest = mateTarget.getPosition();
			updateStats(dt, foodDropBoostFactor);
			MatePairing();
			// More in the sub-classes
		}
	}
	
	private void MatePairing() {
		if(pos.distanceTo(mateTarget.pos) < 8.0) {
			desire = 0.0; mateTarget.desire = 0.0;
			if (baby == null && Utils.RAND.nextDouble() < 0.9) {
				baby = newBaby(mateTarget);
			}
			mateTarget = null;
		}
	}
	
	protected abstract Animal newBaby(Animal p2);
	protected abstract void DeadAction(double dt);
	
	// ----------------------------------
	// GESTIÓN DE ESTADOS
	// ----------------------------------
	
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
	
	// ----------------------------------
	// MÉTODOS PÚBLICOS / IMPLEMENTACIÓN
	// ----------------------------------
	
	public void init(AnimalMapView regMngr) {
		this.regionMngr = regMngr;
		if (this.pos == null) {
			this.pos = createRandomPosition();
		} else {
			adjustPosition();
		}
		this.dest = createRandomPosition();
	}
	
	public Animal deliverBaby() {
		Animal aux = this.baby;
		this.baby = null;
		return aux;
	}
	
	protected void move(double speed) {
		pos = pos.plus(dest.minus(pos).direction().scale(speed));
	}
	
	public JSONObject asJSON() {
		JSONObject jo = new JSONObject();
		
		jo.put("pos", pos.asJSONArray());
		jo.put("gcode", geneticCode);
		jo.put("diet", diet);
		jo.put("state", state);
		
		return jo;
	}
	
	@Override
	public String getGeneticCode() { return geneticCode; }
	
	@Override
	public Diet getDiet() { return diet; }

	@Override
	public State getState() { return state; }

	@Override
	public Vector2D getPosition() { return pos; } // Vector2D es inmutable, seguro devolverlo

	public Vector2D getDestination() { return dest; }
	
	protected void setDestination(Vector2D newDest) { dest = newDest; }

	@Override
	public double getEnergy() { return energy; }
	
	protected void updateEnergy(double newEnergy) {
		energy = Utils.constrainValueInRange(newEnergy, 0.0, 100.0);
	}

	@Override
	public double getSpeed() { return speed; }

	@Override
	public double getAge() { return age; }
	
	protected void updateAge(double addedAge) { age += addedAge; }

	protected double getDesire() { return desire; }
	
	protected void updateDesire(double newDesire) {
		desire = Utils.constrainValueInRange(newDesire, 0.0, 100.0);
	}

	@Override
	public double getSightRange() { return sightRange; }

	@Override
    public boolean isPregnant() { return baby != null; }
}