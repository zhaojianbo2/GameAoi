package game.scene.obj;

public enum SceneObjType {

    PLAYER(1), MONSTER(2);

    private int type;

    public int getType() {
	return type;
    }

    SceneObjType(int type) {
	this.type = type;
    }

}
