package game.ai;

import game.objects.Enemy;

import com.badlogic.gdx.utils.Timer.Task;

public class EnemyTask extends Task{
	public Enemy enemy;
	public EnemyTask(Enemy enemy) {
		this.enemy=enemy;
		
	}
	@Override
	public void run() {
		
		
	}

}
