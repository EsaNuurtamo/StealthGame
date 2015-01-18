package game.tools;

import com.badlogic.gdx.math.Vector2;

public class MyUtils {
	public static boolean isReached(Vector2 v1, Vector2 v2, float tolerance){
		if(Math.abs(v1.x-v2.x)<tolerance&&Math.abs(v1.y-v2.y)<tolerance){
			return true;
			
		}
		return false;
	}
}
