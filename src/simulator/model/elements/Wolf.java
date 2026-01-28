package simulator.model.elements;
import simulator.model.selection.*;

import java.util.List;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Constants;
import simulator.model.Diet;
import simulator.model.State;
import simulator.model.selection.*;

public class Wolf extends Animal{
	
	private Animal huntTarget;
	private SelectionStrategy huntingStrategy;
	
	public Wolf(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy,  Vector2D pos) {
		super(Constants.SHEEP_GENETIC_CODE, Diet.HERBIVORE, 50.0, 60.0, mateStrategy, pos, 
				Constants.FOOD_DROP_RATE_SHEEP, Constants.DESIRE_INCREASE_RATE_SHEEP, 
				Constants.BOOST_FACTOR_SHEEP, Constants.MAX_AGE_SHEEP, 
				Constants.FOOD_DROP_BOOST_FACTOR_SHEEP);
		this.huntTarget = null;
		this.huntingStrategy = dangerStrategy;
	}
	
	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this.huntTarget = null;
		this.huntingStrategy = p1.huntingStrategy;
	}
	
	private Wolf(Wolf other) {
		super(other);
		this.huntTarget = other.huntTarget;
		this.huntingStrategy = other.huntingStrategy;
	}
	
	protected Wolf copyOf() {
		return new Wolf(this);
	}
	
	private void findHuntingTargets() {
		List<Animal> predators = regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.HERBIVORE);
		huntTarget = huntingStrategy.select(this, predators);
	}
	
	
	@Override
	protected void setNormalStateAction() {
		if(getEnergy() < 50.0) {
			setState(State.HUNGER);
		}
		else if(getDesire() > 65.0) {
			super.setState(State.MATE);
		}
	}

	@Override
	protected void DangerAction(double dt) {}
	
	@Override
	protected void setDangerStateAction() {}

	@Override
	protected void MateAction(double dt) {
		super.MateAction(dt);
		setState(State.MATE);
	}
	
	protected Animal newBaby(Animal p2) {
		return new Wolf(this, p2);
	}
	
	@Override
	protected void setMateStateAction() {
		if(huntTarget == null) {
			findHuntingTargets();
		}
		if(huntTarget != null) {
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
}
