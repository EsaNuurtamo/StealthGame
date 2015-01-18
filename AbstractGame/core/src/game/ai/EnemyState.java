package game.ai;

import game.objects.Enemy;
import game.tools.MyUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;





public enum EnemyState implements State<Enemy>{
	
	LOOKOUT() {
		
		private float rotation=0;
		private float sector=60;
		
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
			
			enemy.setLookoutTimer(0);
			rotation=enemy.getRotation();
			
			
		}
		
    },
    
    WALK_TO_PATROL() {
    	
    	
        @Override
        public void update(Enemy enemy) {
        	if(enemy.seePlayer()){
				enemy.getStateMachine().changeState(CHASING);
			}
        	if(MyUtils.isReached(enemy.getPosition(), enemy.getPatrolPath()[0], 0.2f)){
        		
        		enemy.getStateMachine().changeState(PATROL);
        	}
        	
        	enemy.walkOnPath();
        }

		@Override
		public void enter(Enemy enemy)
		{
			
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
			
			enemy.setPath(enemy.getPatrolPath());
			enemy.setSpeed(1);
			
			
		}
		
    },
    
    SEARCH() {
    	
        @Override
        public void update(Enemy enemy) {
        	//change state conditions
        	if(enemy.seePlayer()){
        		
				enemy.getStateMachine().changeState(CHASING);
			}
        	if(enemy.getGiveUpTimer()>4){
        		enemy.getStateMachine().changeState(LONG_SEARCH);
        		enemy.setGiveUpTimer(0);
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
			
			enemy.findPathToPlayer();
			enemy.setPathTimer(3);
			enemy.setGiveUpTimer(0);
			enemy.setSpeed(3);
			
		}
    },
    
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
			
			enemy.findPathToPlayer();
			enemy.setPathTimer(3);
			enemy.setGiveUpTimer(0);
			enemy.setSpeed(1);
			
		}
    },
    
    CHASING() {
        @Override
        public void update(Enemy enemy) {
        	if(enemy.getReactionTimer()<0.3f){
        		return;
        	}
        	if(!enemy.seePlayer()){
        		enemy.getStateMachine().changeState(SEARCH);
        	}
        	Vector2 v=enemy.getState().getPlayer().getPosition().cpy().sub(enemy.getPosition()).nor().scl(enemy.getSpeed());
    		enemy.getBody().setLinearVelocity(v);
    		enemy.setImgRotation(v.angle());
        }

		@Override
		public void enter(Enemy enemy) {
			enemy.setReactionTimer(0);
			
			enemy.setSpeed(3);
			
		}
    };

	

	@Override
	public void exit(Enemy entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMessage(Enemy entity, Telegram telegram) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
}
