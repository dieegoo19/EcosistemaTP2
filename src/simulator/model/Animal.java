package simulator.model;

import java.util.List;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {

	public enum State {
		NORMAL, MATE, HUNGER, DANGER, DEAD;
	}

	public enum Diet {
		HERBIVORE, CARNIVORE;
	}

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

	// ----------------------------------------------------------
	// 2. CONSTRUCTORES
	// ----------------------------------------------------------
	protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy,
			Vector2D pos) throws IllegalArgumentException {
		if (geneticCode == null || geneticCode.isEmpty() || sightRange < 0 || initSpeed < 0 || mateStrategy == null)
			throw new IllegalArgumentException("Argumentos no validos para crear un Animal");
		this.age = 0.0;
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
	}

	protected Animal(Animal p1, Animal p2) {
		this.age = 0.0;
		this.geneticCode = p1.getGeneticCode();
		this.diet = p1.getDiet();
		this.sightRange = Utils.getRandomizedParameter((p1.getSightRange() + p2.getSightRange()) / 2,
				Constants.MUTATION_TOLERANCE);
		this.speed = Utils.getRandomizedParameter((p1.getSpeed() + p2.getSpeed()) / 2, Constants.MUTATION_TOLERANCE);
		this.mateStrategy = p2.mateStrategy;
		this.pos = p1.getPosition()
				.plus(Vector2D.getRandomVector(-1, 1).scale(Constants.NEARBY_FACTOR * (Utils.RAND.nextGaussian() + 1)));
		this.state = State.NORMAL;
		this.energy = (p1.getEnergy() + p2.getEnergy()) / 2.0;
		this.desire = 0.0;
		this.dest = null;
		this.mateTarget = null;
		this.baby = null;
		this.regionMngr = null;
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
		if (st == State.DEAD)
			return;
		switch (st) {
		case NORMAL:
			NormalActionCall(dt);
			break;
		case MATE:
			MateActionCall(dt);
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

		if (adjustPosition()) {
			setState(State.NORMAL);
		}

		if (energy <= 0.0 || age > getMaxAge()) {
			setState(State.DEAD);
		}

		if (getState() != State.DEAD) {
			double food = regionMngr.getFood(this, dt);
			updateEnergy(energy + food);
		}
	}

	protected abstract void NormalActionCall(double dt);

	protected abstract void MateActionCall(double dt);

	protected abstract double getMaxAge();

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
	protected void NormalAction(double dt, double foodDropRate, double desireIncreaseRate) {
		firstActionNormal(dt, foodDropRate, desireIncreaseRate);
		changeNormalStateAction();
	}

	protected void firstActionNormal(double dt, double foodDropRate, double desireIncreaseRate) {
		destinationCheck();
		move(speed * dt * Math.exp((energy - Constants.INIT_ENERGY) * Constants.HUNGER_DECAY_EXP_FACTOR));
		updateAge(dt);
		updateEnergy(energy - foodDropRate * dt);
		updateDesire(desire + desireIncreaseRate * dt);
	}

	// --- Mate ---
	protected void MateAction(double dt, double foodDropBoostFactor, double foodDropRate, double desireIncreaseRate,
			double boostFactor, double pregnantProb) {
		if ((mateTarget != null && mateTarget.getState() == State.DEAD) || !inSightRange(mateTarget)) {
			mateTarget = null;
		}
		if (mateTarget == null) {
			findMateTargets();
			if (mateTarget == null) {
				firstActionNormal(dt, foodDropRate, desireIncreaseRate);
			}
		} else {
			dest = mateTarget.getPosition();
			updateStats(dt, foodDropBoostFactor, boostFactor, foodDropRate, desireIncreaseRate);
			MatePairing(pregnantProb);
		}
		changeMateStateAction();
	}

	private void findMateTargets() {
		List<Animal> candidates = regionMngr.getAnimalsInRange(this,
				a -> a.getGeneticCode().equals(this.geneticCode) && a.getState() != State.DEAD);
		mateTarget = mateStrategy.select(this, candidates);
	}

	private void MatePairing(double pregnantProb) {
		if (pos.distanceTo(mateTarget.pos) < Constants.COLLISION_RANGE) {
			desire = 0.0;
			mateTarget.desire = 0.0;
			if (!isPregnant() && Utils.RAND.nextDouble() < pregnantProb) {
				baby = newBaby(mateTarget);
			}
			energyDrain();
			mateTarget.setState(State.NORMAL);
			mateTarget = null;
		}
	}

	protected abstract void energyDrain();

	// --- Abstracts (Hunger, Danger, Dead) ---
	protected abstract void HungerAction(double dt);

	protected abstract void DangerAction(double dt);

	protected abstract void DeadAction(double dt);

	protected void dead() {
		setState(State.DEAD);
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

	private void move(double speed) {
		pos = pos.plus(dest.minus(pos).direction().scale(speed));
	}

	protected void updateStats(double dt, double spFoodDropBoostFactor, double boostFactor, double foodDropRate,
			double desireIncreaseRate) {
		double s = boostFactor * speed * dt
				* Math.exp((energy - Constants.INIT_ENERGY) * Constants.HUNGER_DECAY_EXP_FACTOR);
		move(s);
		updateAge(dt);
		updateEnergy(energy - foodDropRate * spFoodDropBoostFactor * dt);
		updateDesire(desire + desireIncreaseRate * dt);
	}

	private void destinationCheck() {
		if (getPosition().distanceTo(dest) < Constants.COLLISION_RANGE) {
			dest = createRandomPosition();
		}
	}

	private Vector2D createRandomPosition() {
		// if (regionMngr == null) return new Vector2D(0,0);
		double w = regionMngr.getWidth();
		double h = regionMngr.getHeight();
		return new Vector2D(Utils.RAND.nextDouble() * (w - 1), Utils.RAND.nextDouble() * (h - 1));
	}

	private boolean adjustPosition() {
		Vector2D posIni = pos;
		if (regionMngr != null) {
			double width = regionMngr.getWidth();
			double height = regionMngr.getHeight();
			double x = pos.getX();
			double y = pos.getY();

			while (x >= width)
				x = (x - width);
			while (x < 0)
				x = (x + width);
			while (y >= height)
				y = (y - height);
			while (y < 0)
				y = (y + height);

			this.pos = new Vector2D(x, y);
		}
		return !posIni.equals(pos);
	}

	public boolean inSightRange(Animal a) {
		if (a == null)
			return false;
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
	public String getGeneticCode() {
		return geneticCode;
	}

	@Override
	public Animal.Diet getDiet() {
		return diet;
	}

	@Override
	public Animal.State getState() {
		return state;
	}

	@Override
	public Vector2D getPosition() {
		return pos;
	}

	public Vector2D getDestination() {
		return dest;
	}

	protected void setDestination(Vector2D newDest) {
		dest = newDest;
	}

	@Override
	public double getEnergy() {
		return energy;
	}

	protected void updateEnergy(double newEnergy) {
		energy = Utils.constrainValueInRange(newEnergy, 0.0, Constants.MAX_ENERGY);
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	@Override
	public double getAge() {
		return age;
	}

	protected void updateAge(double addedAge) {
		age += addedAge;
	}

	protected double getDesire() {
		return desire;
	}

	protected void updateDesire(double newDesire) {
		desire = Utils.constrainValueInRange(newDesire, 0.0, Constants.MAX_DESIRE);
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