package simulator.model.elements;
import simulator.model.selection.*;
import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Constants;
import simulator.model.Diet;
import simulator.model.State;
import simulator.model.selection.*;

public class Sheep extends Animal{
	
	private Animal dangerSource;
	private SelectionStrategy dangerStrategy;
	
	public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy,  Vector2D pos) {
		super(Constants.SHEEP_GENETIC_CODE, Diet.HERBIVORE, 40.0, 35.0, mateStrategy, pos, 
				Constants.FOOD_DROP_RATE_SHEEP, Constants.DESIRE_INCREASE_RATE_SHEEP, 
				Constants.BOOST_FACTOR_SHEEP, Constants.MAX_AGE_SHEEP, 
				Constants.FOOD_DROP_BOOST_FACTOR_SHEEP);
		this.dangerSource = null;
		this.dangerStrategy = dangerStrategy;
	}
	
	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this.dangerSource = null;
		this.dangerStrategy = p1.getDangerStrategy();
	}
	
	private Sheep(Sheep other) {
		super(other);
		this.dangerSource = other.dangerSource;
		this.dangerStrategy = other.dangerStrategy;
	}
	
	protected Sheep copyOf() {
		return new Sheep(this);
	}
	
	@Override
	protected void setNormalStateAction() {
		if(dangerSource == null) {
			//TODO: Buscar nuevo animal peligroso
		}
		if(dangerSource != null) {
			super.setState(State.DANGER);
		}else if(getDesire() > 65.0) {
			super.setState(State.MATE);
		}
	}

	@Override
	protected void DangerAction(double dt) {
		if(dangerSource != null && getState() == State.DEAD) {
			dangerSource = null;
		}
		if(dangerSource == null) {
			firstActionNormal(dt);
		}
		else {
			setDestination(getPosition().plus(getPosition().minus(dangerSource.getPosition()).direction()));
			updateStats(dt, foodDropBoostFactor);
		}
		setState(State.DANGER);
	}
	
	@Override
	protected void setDangerStateAction() {
		if(dangerSource == null || !inSightRange(dangerSource)) {
			//TODO: Buscar nuevo animal peligroso
			if(dangerSource == null) {
				if(getDesire() < 65.0) {
					setState(State.NORMAL);
				}
				else {
					setState(State.MATE);
				}
			}
		}
	}

	@Override
	protected void MateAction(double dt) {
		super.MateAction(dt);
		setState(State.MATE);
	}
	
	protected Animal newBaby(Animal p2) {
		return new Sheep(this, p2);
	}
	
	@Override
	protected void setMateStateAction() {
		if(dangerSource == null) {
			//TODO: Buscar nuevo animal peligroso
		}
		if(dangerSource != null) {
			super.setState(State.DANGER);
		}else if(getDesire() < 65.0) {
			super.setState(State.NORMAL);
		}
	}
	
	@Override
	protected void HungerAction(double dt) {}

	@Override
	protected void setHungerStateAction() {}
	
	@Override
	protected void DeadAction(double dt) {}
	
	@Override
	protected void setDeadStateAction() {}
	

	private SelectionStrategy getDangerStrategy() {
		return this.dangerStrategy;
	}
	
	public Animal getDangerSource() {
		return this.dangerSource;
	}
}
