package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal{
	
	private Animal dangerSource;
	private SelectionStrategy dangerStrategy;
	
	public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy,  Vector2D pos) {
		super("Sheep", Diet.HERBIVORE, 40.0, 35.0, mateStrategy, pos);
		this.dangerSource = null;
		this.dangerStrategy = dangerStrategy;
	}
	
	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this.dangerSource = null;
		this.dangerStrategy = p1.getDangerStrategy();
	}

	@Override
	public void update(double dt) {
		State st = this.getState();
		switch (st) {
		case NORMAL:
			break;
		case MATE:
			break;
		case HUNGER:
			break;
		case DANGER:
			break;
		case DEAD:
			break;
		}
		
		//TODO ajustar if outOfRange this.setState(State.NORMAL) y mas
		
	}
	
	

	private SelectionStrategy getDangerStrategy() {
		return this.dangerStrategy;
	}
	
	public Animal getDangerSource() {
		return this.dangerSource;
	}

	@Override
	protected void setNormalStateAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setDangerStateAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setMateStateAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setHungerStateAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setDeadStateAction() {
		// TODO Auto-generated method stub
		
	}

	

}
