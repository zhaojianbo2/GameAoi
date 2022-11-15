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
     *这里直径为5,即4X4 16格
     */
    public static int DEFAULT_AOI_Diameter = 5;
    /**
     * 九宫格算法场景区域宽度
     */
    public static int AREA_WIDTH = WIDTH / DEFAULT_AOI_Diameter;
    /**
     * 九宫格算法场景区域高度
     */
    public static int AREA_HEIGHT = HEIGHT / DEFAULT_AOI_Diameter;
    
    /**
     * 移动速度
     */
    public static int MOVE_SPEED = 100;

    public static int RES_OBJ_INFO_MSG = 2001;
}
