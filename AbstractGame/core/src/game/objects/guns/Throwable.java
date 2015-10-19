package game.objects.guns;

import game.objects.Updatable;

public class Throwable implements Updatable{
	public static final int GRENADE=0;
    public static final int MOLOTOV=1;
    
    private int ammo=0;
    private boolean ready=true;
    private int maxAmmo=10;
    
	@Override
	public void update(float delta) {
		
		
	}
    
    
}
