package game.scene;

import game.scene.obj.Player;
import io.netty.util.AttributeKey;

public class SceneConst {
    public static int DEFAULT_AOI_Diameter = 2;
    /**
     * 九宫格算法场景区域宽度
     */
    public static int AREA_WIDTH = 10;
    /**
     * 九宫格算法场景区域高度
     */
    public static int AREA_HEIGHT = 10;
    
    /**
     * 移动速度
     */
    public static int MOVE_SPEED = 1;
    //玩家modelId
    public static final int playerModelId = 100;
    
    public static AttributeKey<Player> attrPlayer = AttributeKey.newInstance("player");
}
