package game.ai;

import java.util.PriorityQueue;
import java.util.Queue;

import game.objects.Updatable;
import game.objects.guns.Gun;

import com.badlogic.gdx.math.Vector2;

public class EnemyAi implements Updatable{

	private Queue<Action> tasks = new PriorityQueue<Action>();
	//path
	private Vector2[] patrolPath;
	private Vector2[] path;
	private int waypoint=0;
	float tolerance=0.2f;
	float pathLength=0;
	float finderInterval=1f;
	private Gun curGun;
	private float shootInterval;
	
	//ai
	private AIType aiState=AIType.WAITING;
	private float pathTimer=0;
	private float giveUpTimer=0;
	private float lookoutTimer=0;
	private float turnTimer=0;
	private float reactionTimer=0;
	
	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub
		
	}

}
