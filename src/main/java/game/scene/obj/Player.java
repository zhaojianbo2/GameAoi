package game.scene.obj;

import game.scene.SceneConst;
import io.netty.channel.ChannelHandlerContext;

public class Player extends SceneObject {

    public ChannelHandlerContext ctx;

    public Player(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.modelId = SceneConst.playerModelId;
        this.position = new Position(100, 100);
        this.id = System.currentTimeMillis() ;
        this.sceneObjType = SceneObjType.PLAYER;
    }
}
