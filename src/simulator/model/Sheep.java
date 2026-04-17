package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class Sheep extends Animal {

	private Animal dangerSource;
	private SelectionStrategy dangerStrategy;

	// ----------------------------------------------------------
	// CONSTRUCTORES
	// ----------------------------------------------------------
	public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy, Vector2D pos)
			throws IllegalArgumentException {
		super(Constants.SHEEP_GENETIC_CODE, Diet.HERBIVORE, 40.0, 35.0, mateStrategy, pos);
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

	protected void NormalActionCall(double dt) {
		super.NormalAction(dt, Constants.FOOD_DROP_RATE_SHEEP, Constants.DESIRE_INCREASE_RATE_SHEEP);
	}

	protected void MateActionCall(double dt) {
		super.MateAction(dt, Constants.FOOD_DROP_BOOST_FACTOR_SHEEP, Constants.FOOD_DROP_RATE_SHEEP,
				Constants.DESIRE_INCREASE_RATE_SHEEP, Constants.BOOST_FACTOR_SHEEP,
				Constants.PREGNANT_PROBABILITY_SHEEP);
	}

	protected double getMaxAge() {
		return Constants.MAX_AGE_SHEEP;
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
		super.setNormalStateAction();
	}

	@Override
	protected void setMateStateAction() {
		this.dangerSource = null;
	}

	@Override
	protected void setHungerStateAction() {
	}

	@Override
	protected void setDeadStateAction() {
		this.setNormalStateAction();
	}

	// ----------------------------------------------------------
	// LÓGICA DE TRANSICIÓN DE ESTADOS (CHANGE)
	// ----------------------------------------------------------
	@Override
	protected void changeNormalStateAction() {
		if (dangerSource == null) {
			findDangerousTargets();
		}
		if (dangerSource != null) {
			super.setState(State.DANGER);
		} else if (getDesire() > Constants.DESIRE_THRESHOLD_SHEEP) {
			super.setState(State.MATE);
		}
	}

	@Override
	protected void changeMateStateAction() {
		if (dangerSource == null) {
			findDangerousTargets();
		}
		if (dangerSource != null) {
			super.setState(State.DANGER);
		} else if (getDesire() < Constants.DESIRE_THRESHOLD_SHEEP) {
			super.setState(State.NORMAL);
		}
	}

	private void changeDangerStateAction() {
		if (dangerSource == null || !inSightRange(dangerSource)) {
			findDangerousTargets();
			if (dangerSource == null) {
				if (getDesire() < Constants.DESIRE_THRESHOLD_SHEEP) {
					setState(State.NORMAL);
				} else {
					setState(State.MATE);
				}
			}
		}
	}

	// ----------------------------------------------------------
	// ACCIONES ESPECÍFICAS
	// ----------------------------------------------------------

	@Override
	protected void energyDrain() {
	}

	@Override
	protected void DangerAction(double dt) {
		if (dangerSource != null && dangerSource.getState() == State.DEAD) {
			dangerSource = null;
		}
		if (dangerSource == null) {
			firstActionNormal(dt, Constants.FOOD_DROP_RATE_SHEEP, Constants.DESIRE_INCREASE_RATE_SHEEP);
		} else {
			setDestination(getPosition().plus(getPosition().minus(dangerSource.getPosition()).direction()));
			updateStats(dt, Constants.FOOD_DROP_BOOST_FACTOR_SHEEP, Constants.BOOST_FACTOR_SHEEP,
					Constants.FOOD_DROP_RATE_SHEEP, Constants.DESIRE_INCREASE_RATE_SHEEP);
		}

		changeDangerStateAction();
	}

	@Override
	protected void HungerAction(double dt) {
	}

	@Override
	protected void DeadAction(double dt) {
	}

	// ----------------------------------------------------------
	// MÉTODOS AUXILIARES
	// ----------------------------------------------------------
	private void findDangerousTargets() {
		List<Animal> predators = regionMngr.getAnimalsInRange(this,
				a -> a.getDiet() == Diet.CARNIVORE && a.getState() != State.DEAD);
		dangerSource = dangerStrategy.select(this, predators);
	}
}