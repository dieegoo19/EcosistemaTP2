package simulator.model.elements;
import simulator.model.selection.*;
import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Diet;
import simulator.model.State;
import simulator.model.selection.*;

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
	protected void NormalAction(double dt) {
		super.NormalAction(dt);
		super.updateEnergy(super.getEnergy() - 20.0*dt);
		super.updateDesire(super.getDesire() - 40.0*dt);
		setState(State.NORMAL);
	}

	@Override
	protected void DangerAction(double dt) {
		if(dangerSource != null && getState() == State.DEAD) {
			dangerSource = null;
		}
		if(dangerSource == null) {
			this.NormalAction(dt);
		}
		else {
			getPosition().plus(getPosition().minus(dangerSource.getPosition()).direction());
		}
		
	}

	@Override
	protected void MateAction(double dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void HungerAction(double dt) {
		
		
	}

	@Override
	protected void DeadAction(double dt) {
		// TODO Auto-generated method stub
		
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
