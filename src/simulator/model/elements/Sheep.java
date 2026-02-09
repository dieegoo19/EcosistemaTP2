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
	
	// ----------------------------------------------------------
	// CONSTRUCTORES
	// ----------------------------------------------------------
	public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy,  Vector2D pos) throws IllegalArgumentException{
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

	@Override
	protected Animal newBaby(Animal p2) {
		return new Sheep(this, p2);
	}
	
	// ----------------------------------------------------------
	// IMPLEMENTACIÓN DE HOOKS DE ESTADO (SET)
	// ----------------------------------------------------------
	@Override
	protected void setNormalStateAction() {
		super.setNormalStateAction();
		this.dangerSource = null;
	}

	@Override
	protected void setDangerStateAction() {
		super.setMateNull();
	}

	@Override
	protected void setMateStateAction() {
		this.dangerSource = null;
	}
	
	@Override
	protected void setHungerStateAction() {}
	
	@Override
	protected void setDeadStateAction() {}

	// ----------------------------------------------------------
	// LÓGICA DE TRANSICIÓN DE ESTADOS (CHANGE)
	// ----------------------------------------------------------
	@Override
	protected void changeNormalStateAction() {
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
	protected void changeMateStateAction() {
		if(dangerSource == null) {
			findDangerousTargets();
		}
		if(dangerSource != null) {
			super.setState(State.DANGER);
		}else if(getDesire() < Constants.DESIRE_THRESHOLD_SHEEP) {
			super.setState(State.NORMAL);
		}
	}
	
	private void changeDangerStateAction() {
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

	// ----------------------------------------------------------
	// ACCIONES ESPECÍFICAS
	// ----------------------------------------------------------
	
	@Override
	protected void energyDrain() {}
	
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
		changeDangerStateAction();
	}
	
	@Override
	protected void HungerAction(double dt) {}

	@Override
	protected void DeadAction(double dt) {}
	
	// ----------------------------------------------------------
	// MÉTODOS AUXILIARES
	// ----------------------------------------------------------
	private void findDangerousTargets() {
		List<Animal> predators = regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.CARNIVORE);
		dangerSource = dangerStrategy.select(this, predators);
	}
}