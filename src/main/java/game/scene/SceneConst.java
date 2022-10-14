package game.scene;

public class SceneConst {
    /**
     * 广播范围 宽
     */
    public static final int WIDTH = 1950 + 300;
    /**
     * 广播范围高
     */
    public static final int HEIGHT = 1100;
    /**
     * 广播区域范围直径(灯塔默认直径)
     */
    public static int DEFAULT_AOI_Diameter = 5;
    /**
     * 区域宽度
     */
    public static int AREA_WIDTH = WIDTH / DEFAULT_AOI_Diameter;
    /**
     * 区域高度
     */
    public static int AREA_HEIGHT = HEIGHT / DEFAULT_AOI_Diameter;
}
