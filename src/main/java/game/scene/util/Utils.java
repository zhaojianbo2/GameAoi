package game.scene.util;

import game.scene.obj.Position;

public class Utils {

    public static int countDistance(Position pos1, Position pos2) {
	return countDistance(pos1.x, pos1.y, pos2.x, pos2.y);
    }

    public static int countDistance(int sx, int sy, int tx, int ty) {
	return (int) Math.sqrt(Math.pow((sx - tx), 2) + Math.pow((sy - ty), 2));
    }

    public static Position countPosition(Position pos1, Position pos2, int dis) {
	double radius = Math.atan2(pos2.y - pos1.y, pos2.y - pos1.y);
	return new Position((int) Math.round(pos1.x + dis * Math.cos(radius)),
		(int) Math.round(pos1.y + dis * Math.sin(radius)));
    }
}
