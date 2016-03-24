package game.ai;

import game.objects.organic.Enemy;

import com.badlogic.gdx.utils.Timer.Task;

	
public class GiveUp extends Task{
	Enemy enemy;
	
	public GiveUp(Enemy enemy) {	
		this.enemy=enemy;
	}
	
	@Override
	public void run() {
		 enemy.getStateMachine().changeState(EnemyState.LONG_SEARCH);
	}
}
	
	
	
	
	


