package simulator.model.elements;
import simulator.model.selection.*;

import java.util.List;

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
		this.dangerStrategy = p1.dangerStrategy;
	}
	
	
	private void findDangerousTargets() {
		List<Animal> predators = regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.CARNIVORE);
		dangerSource = dangerStrategy.select(this, predators);
	}
	
	
	@Override
	protected void setNormalStateAction() {
		if(dangerSource == null) {
			findDangerousTargets();
		}
		if(dangerSource != null) {
			super.setState(State.DANGER);
		}else if(getDesire() > Constants.DESIRE_THRESHOLD_SHEEP) {
			super.setState(State.MATE);
		}
	}

	@Override
	protected void DangerAction(double dt) {
		if(dangerSource != null && dangerSource.getState() == State.DEAD) {
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
			findDangerousTargets();
			if(dangerSource == null) {
				if(getDesire() < Constants.DESIRE_THRESHOLD_SHEEP) {
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
			findDangerousTargets();
		}
		if(dangerSource != null) {
			super.setState(State.DANGER);
		}else if(getDesire() < Constants.DESIRE_THRESHOLD_SHEEP) {
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
