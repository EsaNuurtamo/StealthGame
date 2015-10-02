package game.states;

import game.Content;
import game.MyConst;
import game.objects.Box;
import game.objects.Bullet;
import game.objects.Enemy;
import game.objects.GameObject;
import game.objects.Player;
import game.objects.Updatable;
import game.tools.MapBodyBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xguzm.pathfinding.gdxbridge.NavTmxMapLoader;

import box2dLight.Light;
import box2dLight.RayHandler;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * 
 * @author esa
 *
 */
public class PlayState implements Screen{
	//Objects
	private Player player;
	private List<GameObject> objects;
	
	private TiledMap map;
	private TiledMapRenderer mapRenderer;
	private SpriteBatch batch;
	private SpriteBatch fontbatch;
    private OrthographicCamera camera;
    private OrthographicCamera cameraFont;
    private Viewport viewport;
    private BitmapFont font=new BitmapFont();
    
    
    //Gamehandler
    private Game game;
   
    //Box2d
    private World world=new World(new Vector2(0, 0), true);
    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    private RayHandler rayHandler; 
    
    public PlayState(Game g) {
    	
        this.game=g;  
        RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		Content.loadAnimations();
      
    }
    
    @Override
    public void show() {
    	loadResources();
    	map = new NavTmxMapLoader().load("maps/Map.tmx");
    	//map=new TmxMapLoader().load("maps/Map.tmx");
    	mapRenderer=new OrthogonalTiledMapRenderer(map,1/(MyConst.ORG_TILE_WIDTH*MyConst.TILES_IN_M));
        camera=new OrthographicCamera(MyConst.APP_WIDTH/MyConst.PIX_IN_M*MyConst.VIEW_SCALE, MyConst.APP_HEIGHT/MyConst.PIX_IN_M*MyConst.VIEW_SCALE);
        viewport=new FitViewport(MyConst.APP_WIDTH/MyConst.PIX_IN_M*MyConst.VIEW_SCALE, MyConst.APP_HEIGHT/MyConst.PIX_IN_M*MyConst.VIEW_SCALE, camera);
        batch=new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        
        
        
        
		
		Array<Body> bodies=MapBodyBuilder.buildTiles(map, world);
		
		rayHandler = new RayHandler(world); 	
		rayHandler.setAmbientLight(0.05f, 0.05f, 0.05f, 0.4f);
		rayHandler.setBlurNum(3);
		MapBodyBuilder.buildLights(map, rayHandler);
		
		
		Light.setContactFilter(MyConst.CATEGORY_BULLETS,(short)0,MyConst.MASK_BULLETS);
		createCollisionListener();
		MapBodyBuilder.initFinder(map);
		//object creation
		player=new Player(this,new Vector2(10,7));
        objects=new ArrayList<GameObject>();
		createEnemies();
        /*Enemy e=new Enemy(this,new Vector2(10,15));
        e.setPath(MapBodyBuilder.findPath(e.getPosition(), player.getPosition(), map));
        objects.add(e);*/
        
    }
    
    @Override
    public void render(float delta) {
    	Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
        	System.exit(0);
        }
        
        
        
        
        
        mapRenderer.setView(camera);
        mapRenderer.render();
        debugRenderer.render(world, camera.combined);
        
        //draw textures here
        batch.begin();
        	
            //draw objects
        	player.draw(batch);
        	for(GameObject obj:objects){
        		if(Math.abs(player.getPosition().x-obj.getPosition().x)>camera.viewportWidth/2||
        		   Math.abs(player.getPosition().y-obj.getPosition().y)>camera.viewportHeight/2)
        		{
        			continue;
        		}
            	obj.draw(batch);
            }
        	
        	
        batch.end();
        
        //fps
        //System.out.println(""+1/delta);
        
	    rayHandler.setCombinedMatrix(camera.combined);
	    rayHandler.updateAndRender();
	    world.step(delta, 6, 2);
	    updateObjects(delta);
	    camera.position.set(player.getPosition(), camera.position.z);
	    camera.update();
        
    }
    
    public void updateObjects(float delta){
    	
    	player.update(delta);
    	Iterator i=objects.iterator();
        while(i.hasNext()){
        	GameObject obj=(GameObject)i.next();
        	
        	if(obj.isDestroyed()){
        		obj.dispose();
        		i.remove();
        		world.destroyBody(obj.getBody());
        		
        	}else if(obj instanceof Updatable){
        		((Updatable)obj).update(delta);
        	}
        }
    }
    
    private void createCollisionListener() {
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
            	
                GameObject a = (GameObject)contact.getFixtureA().getBody().getUserData();
                GameObject b = (GameObject)contact.getFixtureB().getBody().getUserData();
                
                if(a!=null&&a.getBody().isBullet()){
                	
                	a.setDying(true);
                	
                    if(b!=null&&b instanceof Player){
                    	b.setDestroyed(true);
                    }
                    
                    if(b!=null&&b instanceof Enemy){
                    	((Bullet)a).setRed(true);
                    	b.setHealth(b.getHealth()-1);
                    	
                    }
                }
                if(b!=null&&b.getBody().isBullet()){
                	b.setDying(true);
                	
                	if(a!=null&&a instanceof Player){
                    	a.setDestroyed(true);;
                    }
                	if(a!=null&&a instanceof Enemy){
                		a.setHealth(a.getHealth()-1);
                		((Bullet)b).setRed(true);
                    }
                }
                
                
                
                
            }

            @Override
            public void endContact(Contact contact) {
                
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

        });
    }
    /**
     * Creates one enemy for every path on the map
     */
    public void createEnemies(){
    	for(Vector2[] vect:MapBodyBuilder.buildPaths(map)){
    		Enemy e=new Enemy(this, vect[0]);
    		e.setPatrolPath(vect);
    		objects.add(e);
    	}
    	Box b=new Box(this, new Vector2(player.getPosition().x, player.getPosition().y));
    	objects.add(b);
    }

    @Override
    public void resize(int w, int h) {
    	//viewport.update((int)(w/(MyConst.PIX_IN_M*MyConst.VIEW_SCALE)), (int)(h/MyConst.PIX_IN_M*MyConst.VIEW_SCALE));
        viewport.update(w, h);   
    }

    

    @Override
    public void hide() {
        
    }

    @Override
    public void pause() {
       
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void dispose() {
        Content.removeAll();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }


    
    
    
    
    
    
   
    
    public void loadResources(){
       
        
        
        
    }

    
    public World getWorld() {
		return world;
	}
    
    
    public List<GameObject> getObjects() {
    	return objects;
    }

   public void setRayHandler(RayHandler rayHandler) {
	this.rayHandler = rayHandler;
   }
   public RayHandler getRayHandler() {
	return rayHandler;
   }
   public Viewport getViewport() {
	return viewport;
   }
   
   public Player getPlayer() {
	return player;
   }
   
   public TiledMap getMap() {
	return map;
}
        
}
