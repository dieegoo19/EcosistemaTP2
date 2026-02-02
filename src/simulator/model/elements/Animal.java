package simulator.model.elements;

import simulator.model.selection.*;
import java.util.List;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.AnimalMapView;
import simulator.model.Constants;
import simulator.model.Diet;
import simulator.model.Entity;
import simulator.model.State;

public abstract class Animal implements Entity, AnimalInfo {
	
	// ----------------------------------------------------------
	// 1. CAMPOS
	// ----------------------------------------------------------
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
	
	// ----------------------------------------------------------
	// 2. CONSTRUCTORES
	// ----------------------------------------------------------
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
		this.energy = Constants.INIT_ENERGY;
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
		this.sightRange = Utils.getRandomizedParameter((p1.getSightRange()+p2.getSightRange())/2, Constants.MUTATION_TOLERANCE);
		this.speed = Utils.getRandomizedParameter((p1.getSpeed()+p2.getSpeed())/2, Constants.MUTATION_TOLERANCE);
		this.mateStrategy = p2.mateStrategy;
		this.pos = p1.getPosition().plus(Vector2D.getRandomVector(-1,1).scale(Constants.NEARBY_FACTOR*(Utils.RAND.nextGaussian()+1)));
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
	
	// ----------------------------------------------------------
	// 3. MÉTODOS DE CICLO DE VIDA (INIT & UPDATE)
	// ----------------------------------------------------------
	public void init(AnimalMapView regMngr) {
		this.regionMngr = regMngr;
		if (this.pos == null) {
			this.pos = createRandomPosition();
		} else {
			adjustPosition();
		}
		this.dest = createRandomPosition();
	}

	@Override
	public void update(double dt) {
		State st = this.getState();
		if(st == State.DEAD) return;
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
		default:
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

	// ----------------------------------------------------------
	// 4. GESTIÓN DE ESTADOS (SET & CHANGE)
	// ----------------------------------------------------------
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
	
	protected void setNormalStateAction() {
		this.mateTarget = null;
	}
	protected void setMateNull() {
		this.mateTarget = null; 
	}
	protected abstract void setDangerStateAction();
	protected abstract void setMateStateAction();
	protected abstract void setHungerStateAction();
	protected abstract void setDeadStateAction();
	
	protected abstract void changeNormalStateAction();
	protected abstract void changeMateStateAction();

	// ----------------------------------------------------------
	// 5. ACCIONES DE COMPORTAMIENTO
	// ----------------------------------------------------------
	
	// --- Normal ---
	protected void NormalAction(double dt) {
		firstActionNormal(dt);
		changeNormalStateAction();
	}
	
	protected void firstActionNormal(double dt) {
		destinationCheck();
		move(speed * dt * Math.exp((energy - Constants.INIT_ENERGY) * Constants.HUNGER_DECAY_EXP_FACTOR));
		updateAge(dt);
		updateEnergy(energy - foodDropRate * dt);
		updateDesire(desire + desireIncreaseRate * dt);
	}
	
	// --- Mate ---
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
		changeMateStateAction();
	}

	private void MatePairing() {
		if(pos.distanceTo(mateTarget.pos) < Constants.COLLISION_RANGE) {
			desire = 0.0; mateTarget.desire = 0.0;
			if (baby == null && Utils.RAND.nextDouble() < 0.9) {
				baby = newBaby(mateTarget);
			}
			mateTarget = null;
		}
	}
	
	// --- Abstracts (Hunger, Danger, Dead) ---
	protected abstract void HungerAction(double dt);
	protected abstract void DangerAction(double dt);
	protected abstract void DeadAction(double dt);
	
	protected void dead() {
		state = State.DEAD;
	}

	// ----------------------------------------------------------
	// 6. MÉTODOS AUXILIARES Y LÓGICA INTERNA
	// ----------------------------------------------------------
	protected abstract Animal newBaby(Animal p2);
	
	public Animal deliverBaby() {
		Animal aux = this.baby;
		this.baby = null;
		return aux;
	}
	
	protected void move(double speed) {
		pos = pos.plus(dest.minus(pos).direction().scale(speed));
	}
	
	protected void updateStats(double dt, double spFoodDropBoostFactor) {
		double s = boostFactor * speed * dt * Math.exp((energy - Constants.INIT_ENERGY) * Constants.HUNGER_DECAY_EXP_FACTOR);
		move(s);
		updateAge(dt);
		updateEnergy(energy - foodDropRate * spFoodDropBoostFactor * dt);
		updateDesire(desire + desireIncreaseRate * dt);
	}
	
	private void destinationCheck() {
		if (dest == null || getPosition().distanceTo(dest) < Constants.COLLISION_RANGE) {
			dest = createRandomPosition();
		}
	}
	
	private Vector2D createRandomPosition() {
		if (regionMngr == null) return new Vector2D(0,0);
		double w = regionMngr.getWidth();
		double h = regionMngr.getHeight();
		return new Vector2D(Utils.RAND.nextDouble() * (w-1), Utils.RAND.nextDouble() * (h-1));
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
	
	protected boolean inSightRange(Animal a) {
		if (a == null) return false;
		return this.getPosition().distanceTo(a.getPosition()) <= this.sightRange;
	}

	// ----------------------------------------------------------
	// 7. GETTERS, SETTERS & JSON
	// ----------------------------------------------------------
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
	public Vector2D getPosition() { return pos; }
	
	public Vector2D getDestination() { return dest; }
	protected void setDestination(Vector2D newDest) { dest = newDest; }

	@Override
	public double getEnergy() { return energy; }
	protected void updateEnergy(double newEnergy) {
		energy = Utils.constrainValueInRange(newEnergy, 0.0, Constants.MAX_ENERGY);
	}

	@Override
	public double getSpeed() { return speed; }

	@Override
	public double getAge() { return age; }
	protected void updateAge(double addedAge) { age += addedAge; }

	protected double getDesire() { return desire; }
	protected void updateDesire(double newDesire) {
		desire = Utils.constrainValueInRange(newDesire, 0.0, Constants.MAX_DESIRE);
	}

	@Override
	public double getSightRange() { return sightRange; }

	@Override
    public boolean isPregnant() { return baby != null; }
}