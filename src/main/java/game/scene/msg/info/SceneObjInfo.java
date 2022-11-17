package game.scene.msg.info;

import java.util.List;

public class SceneObjInfo {
    public long objId;// 唯一id
    public int modelId;// 模型sid
    public PointInfo currentPosition;//当前位置
    public List<PointInfo> roads;//跑步路径集合
}
