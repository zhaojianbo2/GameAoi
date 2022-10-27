package game.scene.obj;

import java.util.ArrayList;
import java.util.List;

import game.scene.AbstractScene;

public abstract class SceneObject {
    // 对象唯一id
    public long id;
    // 当前所在场景引用
    public AbstractScene currentScene;
    // 当前坐标
    public Position position;
    // 对象类型
    public SceneObjType sceneObjType;
    // 上次跑动时间戳
    public long preStepTime;
    // 跑动路径
    public List<Position> runningRoads = new ArrayList<>();
}
