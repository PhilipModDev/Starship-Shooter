package com.engine.starship.utils;

import com.badlogic.gdx.math.Vector2;
import com.engine.starship.utils.entities.PathFinding;

public class Utils {

    public static float manhattanDistance(Vector2 v1, Vector2 v2){
        return Math.abs(v1.x - v2.x) + Math.abs(v1.y - v2.y);
    }
    public static int manhattanDistance(PathFinding.Node n1, PathFinding.Node n2){
        return Math.abs(n1.x - n2.x) + Math.abs(n1.y - n2.y);
    }
}
