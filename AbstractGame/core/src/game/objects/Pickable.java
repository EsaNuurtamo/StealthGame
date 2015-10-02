package game.objects;

import game.states.PlayState;

import com.badlogic.gdx.math.Vector2;

public class Pickable extends GameObject{
	public Pickable(PlayState state, Vector2 position) {
		super(state, position);
	}
}
