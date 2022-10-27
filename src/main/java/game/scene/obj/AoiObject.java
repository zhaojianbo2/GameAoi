package game.scene.obj;

import java.util.List;

/**
 * 
 * @author WinkeyZhao
 *
 *         aoi对象,这里为了绑定一个该对象当前aoi areaId 绑定,不用每次进行计算
 */
public class AoiObject {
    public SceneObject sceneObject;
    public List<Long> aoiAreaList;

    public AoiObject(SceneObject sceneObject, List<Long> aoiAreaList) {
	this.sceneObject = sceneObject;
	this.aoiAreaList = aoiAreaList;
    }
    
    public void setAoiAreaList(List<Long> targetAoiList) {
	aoiAreaList.clear();
	aoiAreaList.addAll(targetAoiList);
    }
}
