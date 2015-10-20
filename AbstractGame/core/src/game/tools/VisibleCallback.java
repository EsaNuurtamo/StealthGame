package game.tools;

import game.MyConst;
import game.objects.GameObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class VisibleCallback implements RayCastCallback{
	GameObject targetObj;
    boolean isVisible;
    
    public VisibleCallback(){
    	this(null);
    }
	public VisibleCallback(GameObject go) {
		targetObj=go;
		isVisible=true;
	}
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,Vector2 normal, float fraction) {
		if(fixture.getFilterData().categoryBits==MyConst.CATEGORY_BULLETS)return 1;
		if(fixture.getBody().getUserData()==null){
			
			return -1;
		}
		
		if(fixture.getBody().getUserData().equals(targetObj)){
				
			return fraction;
		}else{
			
			isVisible=false;
			return fraction;
		}
		
		
	}
	
	public boolean isVisible(){
		return isVisible;
	}
	
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	public void setTargetObj(GameObject targetObj) {
		this.targetObj = targetObj;
	}

}
