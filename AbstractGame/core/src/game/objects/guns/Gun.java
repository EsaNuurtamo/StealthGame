
package game.objects.guns;


import game.MyConst;
import game.objects.Bullet;
import game.objects.GameObject;
import game.objects.Player;
import game.objects.Updatable;
import box2dLight.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;


public abstract class Gun implements Updatable{
    public static final int PISTOL=0;
    public static final int MG=1;
    public static final int RPG=2;
    public static final int SHOTGUN=3;
    
    protected boolean carried=false;
    protected Sound shootSFX;
    protected Sound reloadSFX;
    protected int ammo;
    protected int clipSize;
    protected int inClip;
    protected boolean empty=false;
    protected boolean ready=false;
    protected boolean reloading=false;
    protected float speed=10f;
    protected float reloadtime;
    protected float fireRate;
    protected float reloadTimer=0;
    protected float fireRateTimer=0;
    protected GameObject shooter;

    public Gun(GameObject shooter) {
        this.shooter=shooter;
    }
    
    public void reload(){
    	reloadSFX.play(MyConst.volumeSFX);
    	reloading=true;
    }
    
    public void update(float delta){
    	if(empty){
    		reload();
    		empty=false;
    	}
        if(reloading){
        	reloadTimer+=delta;
            
        }else if(!ready){
            fireRateTimer+=delta;
        }
        
        //lipppaan lataaminen
        if(reloadTimer>=reloadtime){
            if(ammo<=0)return;
            reloadTimer=0;
            reloading=false;
            if(ammo<clipSize){
                inClip=ammo;
                ammo=0;
            }else{
                inClip=clipSize;
                ammo-=clipSize;
            }
        }
        
        //luodin lataaminen piippuun
        if(fireRateTimer>=fireRate){
            fireRateTimer=0;
            ready=true;
        }
        
        
    }
  //offse from recoil
  		
    public void aiShoot(Vector2 direction,float offset){
    	float turn=(float) MyConst.RAND.nextGaussian()*offset;
    	pullTrigger(direction.cpy().rotate(turn));
    	
    }
    public void pullTrigger(Vector2 direction){
        if(reloading)return;
        
        //ampuminen
        if(ready&&!empty){
            shoot(direction);
            inClip--;
            if(inClip<=0){
                empty=true;
                
            }
            ready=false;
        }
        
    }

    public int getAmmo() {
        return ammo;
    }

    public int getInClip() {
        return inClip;
    }
    public void addAmmo(int num){
    	ammo+=num;
    }
    public void shoot(Vector2 direction){
    	
    	if(shootSFX!=null)shootSFX.play(MyConst.volumeSFX);
    	spawnBullet(direction);
    	
    }
    
    public void spawnBullet(Vector2 direction){
    	shooter.alertAllNear();
    	Bullet b=new Bullet(shooter.getState(), shooter.getPosition().cpy().add(shooter.getDirection().cpy().limit(shooter.getRadius()+0.2f)),shooter.isFriendly());
    	
    	b.setDirection(direction.nor());
    	b.setImgRotation(direction.angle()-90);
    	
    	
    	//b.getBody().setLinearVelocity(direction.cpy().limit(10));
    	shooter.getState().addObj(b);
    }
    
}
