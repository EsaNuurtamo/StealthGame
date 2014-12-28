package game;

public class MyConst {
	
	public static final int APP_WIDTH = 1920;
    public static final int APP_HEIGHT = 1080;
    public static final float VIEW_SCALE=1f;
    public static final float MAP_SCALE=1f;
    public static final float PIX_IN_M=100;
    public static final float ORG_TILE_WIDTH=64;
    public static final float TILES_IN_M=1f;
    
    public static final short CATEGORY_PLAYER = 1;  
    public static final short CATEGORY_BULLETS = 2; 
    public static final short CATEGORY_SCENERY = 4;
    public static final short CATEGORY_ENEMY = 8;
    public static final short MASK_BULLETS=CATEGORY_ENEMY|CATEGORY_PLAYER|CATEGORY_SCENERY;
    public static final short MASK_PLAYER=CATEGORY_ENEMY|CATEGORY_SCENERY|CATEGORY_BULLETS;
}
