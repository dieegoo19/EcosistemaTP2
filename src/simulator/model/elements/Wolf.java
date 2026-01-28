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
		super(Constants.WOLF_GENETIC_CODE, Diet.CARNIVORE, 50.0, 60.0, mateStrategy, pos, 
				Constants.FOOD_DROP_RATE_WOLF, Constants.DESIRE_INCREASE_RATE_WOLF, 
				Constants.BOOST_FACTOR_WOLF, Constants.MAX_AGE_WOLF, 
				Constants.FOOD_DROP_BOOST_FACTOR_WOLF);
		this.huntTarget = null;
		this.huntingStrategy = dangerStrategy;
	}
	
	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this.huntTarget = null;
		this.huntingStrategy = p1.huntingStrategy;
	}
	
	private void findHuntingTargets() {
		List<Animal> preys = regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.HERBIVORE);
		huntTarget = huntingStrategy.select(this, preys);
	}
	
	@Override
	protected void setNormalStateAction() {
		if(getEnergy() < Constants.FOOD_EAT_VALUE_WOLF) {
			setState(State.HUNGER);
		}
		else if(getDesire() > Constants.DESIRE_THRESHOLD_WOLF) {
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
		if(getEnergy() < 50.0) {
			super.setState(State.HUNGER);
		}else if(getDesire() < 65.0) {
			super.setState(State.NORMAL);
		}
	}
	
	@Override
	protected void HungerAction(double dt) {
		if(huntTarget == null || huntTarget.getState() == State.DEAD) {
			findHuntingTargets();
		}
		if(huntTarget == null) {
			firstActionNormal(dt);
		}else {
			setDestination(huntTarget.getPosition());
			updateStats(dt, foodDropBoostFactor);
			Hunt();
		}
		if(getEnergy() > 50.0) setState(State.HUNGER);
	}
	
	private void Hunt() {
		if(getPosition().distanceTo(huntTarget.getPosition()) < Constants.COLLISION_RANGE) {
			huntTarget.dead();
			huntTarget = null;
			updateEnergy(getEnergy() + 50.0);
		}
	}

	@Override
	protected void setHungerStateAction() {
		if(getDesire() < Constants.DESIRE_THRESHOLD_WOLF) {
			setState(State.NORMAL);
		}else {
			setState(State.MATE);
		}
	}
	
	@Override
	protected void DeadAction(double dt) {}
	
	@Override
	protected void setDeadStateAction() {}
}
