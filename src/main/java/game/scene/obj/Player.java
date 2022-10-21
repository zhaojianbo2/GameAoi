package game.scene.obj;

import io.netty.channel.ChannelHandlerContext;

public class Player extends SceneObject {
    private ChannelHandlerContext ctx;

    public Player(ChannelHandlerContext ctx) {
	this.ctx = ctx;
	this.position = new Position(100, 100);
	this.id = System.currentTimeMillis() / 1000l;
	this.sceneObjType = SceneObjType.PLAYER;
    }
}
