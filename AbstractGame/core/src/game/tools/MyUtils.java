package game.tools;

import game.MyConst;
import game.objects.GameObject;
import game.objects.organic.Player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class MyUtils {
	public static boolean isReached(Vector2 v1, Vector2 v2, float tolerance){
		if(Math.abs(v1.x-v2.x)<tolerance&&Math.abs(v1.y-v2.y)<tolerance){
			return true;
			
		}
		return false;
	}
	
	public static Vector2 toPix(Vector2 v){
		return new Vector2(v.x/MyConst.PIX_IN_M*MyConst.VIEW_SCALE,v.y/MyConst.PIX_IN_M*MyConst.VIEW_SCALE);
	}
	
	public static List<GameObject> objInRange(List<GameObject> all, Vector2 vect, float radius){
		List<GameObject> list=new ArrayList<GameObject>();
		for(GameObject obj:all){
			
			if(obj.getPosition().sub(vect).len()<radius){
				list.add(obj);
			}
		}
		return list;
	}
}


