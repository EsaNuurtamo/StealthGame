package game.ai;

import game.objects.GameObject;
import game.objects.organic.Enemy;
import game.tools.MyUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;





public enum EnemyState implements State<Enemy>{
	
	
	LOOKOUT() {
		
		
		private float sector=80;
		
        @Override
        public void update(Enemy enemy) {
        	
        	if(enemy.seePlayer()){
        		
				enemy.getStateMachine().changeState(CHASING);
			}
			if(enemy.getPatrolPath()!=null&&enemy.getLookoutTimer()>4){
				enemy.getStateMachine().changeState(WALK_TO_PATROL);
				enemy.setLookoutTimer(0);
			}
			
			if(enemy.getTurnTimer()>1){
        		float temp=enemy.getRotation()+(float)Math.random()*sector-0.5f*sector;
        		if(temp<0){
        			temp=360+temp;
        		}
        		if(temp>360){
        			temp=temp-360;
        		}
        		enemy.setTargetRotation(temp);
        	    enemy.setTurnTimer(0);
        	}
			
        }

		@Override
		public void enter(Enemy enemy) {
			enemy.getLight().setColor(Color.CYAN);
			enemy.setLookoutTimer(0);
			
			
			
		}
		@Override
		public boolean onMessage(Enemy entity, Telegram telegram) {
			
			
			return true;
		}
		
    },
    
    WALK_TO_PATROL() {
    	
    	
        @Override
        public void update(Enemy enemy) {
        	if(enemy.seePlayer()){
				enemy.getStateMachine().changeState(CHASING);
			}
        	if(MyUtils.isReached(enemy.getPosition(), enemy.getPatrolPath()[0], 0.5f)){
        		
        		enemy.getStateMachine().changeState(PATROL);
        	}
        	if(enemy.getPathfindTimer()>3f){
        		enemy.findPathTo(enemy.getPatrolPath()[0]);
        		enemy.setPathfindTimer(0);
        	}
        	enemy.walkOnPath();
        }

		@Override
		public void enter(Enemy enemy)
		{
			enemy.setPathfindTimer(0);
			enemy.getLight().setColor(new Color(0,0,1f,0.4f));
			enemy.setSpeed(1);
			Vector2 patrolStart=enemy.getPatrolPath()[0];
			enemy.findPathTo(patrolStart);
			
		}
		
    },

    PATROL() {
    	
        @Override
        public void update(Enemy enemy) {
        	if(enemy.seePlayer()){
				enemy.getStateMachine().changeState(CHASING);
			}
        	enemy.walkOnPath();
        }

		@Override
		public void enter(Enemy enemy)
		{
			enemy.getLight().setColor(Color.PINK);;
			
			enemy.setPath(enemy.getPatrolPath());
			enemy.setSpeed(1);
		}
		
    },
    
    //searching the player after loosing him
    SEARCH() {
    	
        @Override
        public void update(Enemy enemy) {
        	//change state conditions
        	if(enemy.getGiveUpTimer()>7){
        		enemy.getStateMachine().changeState(LONG_SEARCH);
        		enemy.setGiveUpTimer(0);
        	}
        	if(enemy.seePlayer()){
        		
				enemy.getStateMachine().changeState(CHASING);
			}
        	
        	
        	//timedEvents
        	if(enemy.getPathTimer()>1){
        		enemy.findPathToPlayer();
        		enemy.setPathTimer(0);
        	}
        	
        	
        	
        	enemy.walkOnPath();
        	
        }

		@Override
		public void enter(Enemy enemy) {
			
			enemy.getLight().setColor(new Color(1f,1f,0,0.4f));
			enemy.findPathToPlayer();
			enemy.setPathTimer(3);
			enemy.setShootTimer(0);
			enemy.setSpeed(3);
			
			//timed statechange
			enemy.setGiveUpTimer(0);
			
		}
    },
    
    //more passive searching when enemy has searched for long already
    LONG_SEARCH() {
    	
        @Override
        public void update(Enemy enemy) {
        	//change state conditions
        	if(enemy.seePlayer()){
        		
				enemy.getStateMachine().changeState(CHASING);
			}
        	if(enemy.getGiveUpTimer()>10){
        		enemy.getStateMachine().changeState(LOOKOUT);
        		enemy.setGiveUpTimer(0);
        	}
        	
        	//timedEvents
        	if(enemy.getPathTimer()>2){
        		Vector2 v=enemy.getState().getPlayer().getPosition().cpy();
        		//v.add((float)Math.random()*4-2, (float)Math.random()*4-2);
        		enemy.findPathTo(v);
        		enemy.setPathTimer(0);
        	}
        	
        	
        	
        	enemy.walkOnPath();
        	
        }

		@Override
		public void enter(Enemy enemy) {
			
			enemy.getLight().setColor(new Color(1f,1f,0,0.4f));
			enemy.findPathToPlayer();
			enemy.setPathTimer(3);
			enemy.setGiveUpTimer(0);
			enemy.setSpeed(1);
			//Timer.schedule(enemy.giveUp(), 10f);
		}
    },
    
    CHASING() {
        @Override
        public void update(Enemy enemy) {
        	
        	
        	
        	Vector2 v=enemy.getState().getPlayer().getPosition().cpy().sub(enemy.getPosition()).nor().scl(enemy.getSpeed());
        	if(enemy.getPosition().dst(enemy.getState().getPlayer().getPosition())>3)enemy.getBody().setLinearVelocity(v);
    		enemy.setTargetRotation(v.angle());
    	
        	if(enemy.getShootTimer()>0.2f){
        		enemy.setShootTimer(enemy.getShootTimer()-0.2f);
        		enemy.shootPlayer();
        	}
        	
        	
        	
        	if(!enemy.seePlayer()){
        		//System.out.println("nosee");
        		enemy.getStateMachine().changeState(SEARCH);
        	}
        	//if(enemy.getPosition().dst(enemy.getState().getPlayer().getPosition())<3)return;
        	
        }

		@Override
		public void enter(Enemy enemy) {
			//enemy.throwGranade();
			if(!enemy.getState().getCurMusic().isPlaying())enemy.getState().getCurMusic().play();
			enemy.setReactionTimer(0);
			enemy.getLight().setColor(new Color(1f,0,0,0.4f));
			enemy.setSpeed(3);
			enemy.setShootTimer(0);
			//all near enemies join on search
			//tee vihulle ringalarm joka vaihtaa playstateen tilaksi alarmed ja hoida siellä tämä
			for(GameObject obj:MyUtils.objInRange(enemy.getState().getObjects(), enemy.getPosition(), 5f)){
				if(obj instanceof Enemy&&!((Enemy)obj).getStateMachine().isInState(CHASING)&&!((Enemy)obj).getStateMachine().isInState(SEARCH)
				   ){
					((Enemy) obj).getStateMachine().changeState(SEARCH);
				}
			}
			
		}
    };

	@Override
	public void enter(Enemy entity) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void exit(Enemy entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMessage(Enemy entity, Telegram telegram) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	///enemy state death
	
}
