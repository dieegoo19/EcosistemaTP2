package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class Wolf extends Animal {

	private Animal huntTarget;
	private SelectionStrategy huntingStrategy;

	// ----------------------------------------------------------
	// CONSTRUCTORES
	// ----------------------------------------------------------
	public Wolf(SelectionStrategy mateStrategy, SelectionStrategy huntingStrategy, Vector2D pos)
			throws IllegalArgumentException {
		super(Constants.WOLF_GENETIC_CODE, Diet.CARNIVORE, 50.0, 60.0, mateStrategy, pos);
		this.huntTarget = null;
		this.huntingStrategy = huntingStrategy;
	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this.huntTarget = null;
		this.huntingStrategy = p1.huntingStrategy;
	}

	@Override
	protected Animal newBaby(Animal p2) {
		return new Wolf(this, p2);
	}

	protected void NormalActionCall(double dt) {
		super.NormalAction(dt, Constants.FOOD_DROP_RATE_WOLF, Constants.DESIRE_INCREASE_RATE_WOLF);
	}

	protected void MateActionCall(double dt) {
		super.MateAction(dt, Constants.FOOD_DROP_BOOST_FACTOR_WOLF, Constants.FOOD_DROP_RATE_WOLF,
				Constants.DESIRE_INCREASE_RATE_WOLF, Constants.BOOST_FACTOR_WOLF, Constants.PREGNANT_PROBABILITY_WOLF);
	}

	protected double getMaxAge() {
		return Constants.MAX_AGE_WOLF;
	}

	// ----------------------------------------------------------
	// IMPLEMENTACIÓN DE HOOKS DE ESTADO (SET)
	// ----------------------------------------------------------
	@Override
	protected void setNormalStateAction() {
		super.setNormalStateAction();
		this.huntTarget = null;
	}

	@Override
	protected void setMateStateAction() {
		this.huntTarget = null;
	}

	@Override
	protected void setHungerStateAction() {
		super.setNormalStateAction();
	}

	@Override
	protected void setDangerStateAction() {
	}

	@Override
	protected void setDeadStateAction() {
	}

	// ----------------------------------------------------------
	// LÓGICA DE TRANSICIÓN DE ESTADOS (CHANGE)
	// ----------------------------------------------------------
	@Override
	protected void changeNormalStateAction() {
		if (getEnergy() < Constants.FOOD_EAT_VALUE_WOLF) {
			setState(State.HUNGER);
		} else if (getDesire() > Constants.DESIRE_THRESHOLD_WOLF) {
			super.setState(State.MATE);
		}
	}

	@Override
	protected void changeMateStateAction() {
		if (getEnergy() < 50.0) {
			super.setState(State.HUNGER);
		} else if (getDesire() < 65.0) {
			super.setState(State.NORMAL);
		}
	}

	private void changeHungerStateAction() {
		if (getDesire() < Constants.DESIRE_THRESHOLD_WOLF) {
			setState(State.NORMAL);
		} else {
			setState(State.MATE);
		}
	}

	// ----------------------------------------------------------
	// ACCIONES ESPECÍFICAS
	// ----------------------------------------------------------

	@Override
	protected void energyDrain() {
		super.updateEnergy(this.getEnergy() - 10.0);
	}

	@Override
	protected void HungerAction(double dt) {
		if (huntTarget == null || (huntTarget != null && huntTarget.getState() == State.DEAD)
				|| !this.inSightRange(huntTarget)) {
			findHuntingTargets();
		}
		if (huntTarget == null) {
			firstActionNormal(dt, Constants.FOOD_DROP_RATE_WOLF, Constants.DESIRE_INCREASE_RATE_WOLF);
		} else {
			setDestination(huntTarget.getPosition());
			updateStats(dt, Constants.FOOD_DROP_BOOST_FACTOR_WOLF, Constants.BOOST_FACTOR_WOLF,
					Constants.FOOD_DROP_RATE_WOLF, Constants.DESIRE_INCREASE_RATE_WOLF);
			if (getPosition().distanceTo(huntTarget.getPosition()) < Constants.COLLISION_RANGE)
				Hunt();
		}
		if (getEnergy() > 50.0)
			changeHungerStateAction();
	}

	@Override
	protected void DangerAction(double dt) {
	}

	@Override
	protected void DeadAction(double dt) {
		this.setNormalStateAction();
	}

	// ----------------------------------------------------------
	// MÉTODOS AUXILIARES
	// ----------------------------------------------------------
	private void findHuntingTargets() {
		List<Animal> preys = regionMngr.getAnimalsInRange(this,
				a -> a.getDiet() == Diet.HERBIVORE && a.getState() != State.DEAD);
		huntTarget = huntingStrategy.select(this, preys);
	}

	private void Hunt() {
		if (huntTarget.getState() != State.DEAD) {
			huntTarget.dead();
			updateEnergy(getEnergy() + Constants.FOOD_EAT_VALUE_WOLF);
		}
		huntTarget = null;
	}
}