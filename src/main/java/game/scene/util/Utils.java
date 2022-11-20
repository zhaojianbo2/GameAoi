package game.scene.util;

import game.scene.obj.Position;

public class Utils {

    public static double countDistance(Position pos1, Position pos2) {
        return countDistance(pos1.x, pos1.y, pos2.x, pos2.y);
    }

    public static double countDistance(double sx, double sy, double tx, double ty) {
        return Math.sqrt(Math.pow((sx - tx), 2) + Math.pow((sy - ty), 2));
    }

    public static Position countPosition(Position pos1, Position pos2, double dis) {
        double radius = Math.atan2(pos2.y - pos1.y, pos2.x - pos1.x);
        return new Position(pos1.x + dis * Math.cos(radius),
            pos1.y + dis * Math.sin(radius));
    }
}
