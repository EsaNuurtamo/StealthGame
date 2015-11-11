package game.tools;

import game.MyConst;
import game.objects.Box;
import game.objects.Wall;
import game.states.PlayState;

import java.util.List;

import org.xguzm.pathfinding.gdxbridge.NavigationTiledMapLayer;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;



public class MapBodyBuilder {

    // The pixels per tile. If your tiles are 16x16, this is set to 16f
	private static float ppt=MyConst.ORG_TILE_WIDTH*MyConst.TILES_IN_M;
    private static int tileNum=60;
    public static NavigationTiledMapLayer navGrid;
    public static AStarGridFinder<GridCell> finder;
    
    public static Array<Body> buildShapes(Map map, World world) {
    	
    	MapLayer tl=map.getLayers().get("Physics");
    	
        MapObjects objects = tl.getObjects();

        Array<Body> bodies = new Array<Body>();

        for(MapObject object : objects) {
        
            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;

            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject)object);
            }
            else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject)object);
            }
            else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject)object);
            }
            else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject)object);
            }
            else {
                continue;
            }

            BodyDef bd = new BodyDef();
            bd.type = BodyType.StaticBody;
            Body body = world.createBody(bd);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density=1;
            
            body.createFixture(fixtureDef);

            bodies.add(body);

            shape.dispose();
        }
        return bodies;
    }
    
    public static void buildObjects(Map map,PlayState state){
    	MapLayer tl=map.getLayers().get("Objects");
    	for(MapObject obj : tl.getObjects()){
    		
    		TiledMapTileMapObject object = (TiledMapTileMapObject)obj;
    		if(object.getTile().getProperties().get("type").equals("health_")){
    			state.addObj(new Box(state,new Vector2(object.getX(),object.getY())));
    		}
    	}
    }
    
    public static void buildLights(Map map, RayHandler ray,PlayState state){
    	MapLayer tl=map.getLayers().get("Lights");
    	for(MapObject obj: tl.getObjects()){
    		Ellipse c=((EllipseMapObject)obj).getEllipse();
    		if(c==null)continue;
    		Integer dist=null;
    		if(obj.getProperties().get("distance")==null){
    			dist=6;
    		}else{
    			dist=Integer.parseInt(obj.getProperties().get("distance").toString());
    		}
    		PointLight point=new PointLight(ray, dist*50, new Color(0.2f,0.2f,0.2f,0.1f), (dist+2), c.x/ppt, c.y/ppt);
    		point.setSoftnessLength(1f);
    		point.setStaticLight(true);
    		state.getLights().add(point);
    		PointLight p=new PointLight(ray, dist*50, new Color(1f,1f,1f,0.6f), dist, c.x/ppt, c.y/ppt);
    		p.setSoftnessLength(1f);
    		
    	}  
    }
    
    public static Array<Vector2[]> buildPaths(Map map){
    	Array<Vector2[]> paths=new Array<Vector2[]>();
    	MapLayer tl=map.getLayers().get("Paths");
    	for(MapObject obj: tl.getObjects()){
    		PolylineMapObject p=((PolylineMapObject)obj);
    		if(p==null)continue;
    		paths.add(getPolylineVertices(p));
    	}
    	return paths;
    }
    
    public static Array<Body> buildTiles(Map map, World world, PlayState state){
    	Array<Body> bodies = new Array<Body>();
    	GridCell[][] cells=new GridCell[tileNum][tileNum];
    	navGrid=new NavigationTiledMapLayer(cells);
    	TiledMapTileLayer tl=(TiledMapTileLayer)map.getLayers().get("Tiles");
        for (int i=0; i<tileNum;i++){
            for(int j=0; j<tileNum;j++){
                
                Cell c=tl.getCell(i, j);
                float apu=MyConst.ORG_TILE_WIDTH;
               
                //If no tile or tile is a walkable tile then don't create object. Also sets information to pathfinding system 
                if(c==null||c.getTile()==null||c.getTile().getProperties().get("walkable").toString().equals("1")){
                	navGrid.setCell(i, j, new GridCell(i, j, true));
                	continue;
                }
                
                
                navGrid.setCell(i, j, new GridCell(i, j, false));
                Rectangle r=new Rectangle();
                r.height=apu;
                r.width=apu;
                r.x=i*apu;
                r.y=j*apu;
                
                BodyDef bd = new BodyDef();
                bd.type = BodyType.StaticBody;
                Body body = world.createBody(bd);
                Shape shape=getRectangle(r);
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape=shape;
                fixtureDef.density=1;
                fixtureDef.filter.categoryBits=MyConst.CATEGORY_SCENERY;
                body.createFixture(fixtureDef);
                body.setUserData(new Wall(state,new Vector2(r.x,r.y)));

                bodies.add(body);
                
                shape.dispose();
                
            }
        }
        
       
        return bodies;
    }
    
    private static PolygonShape getRectangle(Rectangle rectangle) {
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / ppt,
                (rectangle.y + rectangle.height * 0.5f ) / ppt);
		polygon.setAsBox(rectangle.width * 0.5f / ppt,
				         rectangle.height * 0.5f / ppt,
				         size,
				         0.0f);
        return polygon;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        return getRectangle(rectangleObject.getRectangle());
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / ppt);
        circleShape.setPosition(new Vector2(circle.x / ppt, circle.y / ppt));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            
            worldVertices[i] = vertices[i] / ppt;
        }

        polygon.set(worldVertices);
        return polygon;
    }
    
    

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        ChainShape chain = new ChainShape(); 
        chain.createChain(getPolylineVertices(polylineObject));
        return chain;
    }
    
    private static Vector2[] getPolylineVertices(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / ppt;
            worldVertices[i].y = vertices[i * 2 + 1] / ppt;
        }

        
        return worldVertices;
    }
    
    public static void initFinder(Map map){
    	GridFinderOptions opt = new GridFinderOptions();
        opt.allowDiagonal = true;
        finder = new AStarGridFinder(GridCell.class, opt);
    }
    
    public static Vector2[] findPath(Vector2 v1, Vector2 v2, Map map){
    	List<GridCell> pathToEnd = finder.findPath((int)(v1.x*MyConst.TILES_IN_M), (int)(v1.y*MyConst.TILES_IN_M), 
				   (int)(v2.x*MyConst.TILES_IN_M), (int)(v2.y*MyConst.TILES_IN_M), navGrid);
    	
    	Vector2[] list=new Vector2[pathToEnd.size()];
    	int index=0;
    	for(GridCell c:pathToEnd){
    	
    		Vector2 v=new Vector2(c.x+0.5f, c.y+0.5f);
    		list[index]=v;
    		index++;
    	}
    	return list;
		
		
    	
    }
    
    
}
