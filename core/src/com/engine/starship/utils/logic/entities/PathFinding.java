package com.engine.starship.utils.logic.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision._btMprSimplex_t;
import com.engine.starship.StarshipShooter;
import com.engine.starship.UniverseManager;
import com.engine.starship.utils.math.Directions;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

public class PathFinding {
    //The max range distance for the path finding algorithm.
    public static final int MAX_TRANSVERSAL_DISTANCE  = 20;
    public static final Directions[] DIRECTIONS = Directions.values();
    //Gets the universe class.
    private final UniverseManager universeManager = StarshipShooter.getInstance().universeManager;
    private Vector2 entityPos;
    private Vector2 playerPos;
    //Gets the path finding class.
    public ArrayList<Node> pathFindEntity(Entity entity) {
        Starship player = universeManager.getPlayer();
        Sprite sprite = entity.getSprite();
        //The circle detection range for the path finding.
        Circle circle = new Circle(sprite.getX(), sprite.getY(), MAX_TRANSVERSAL_DISTANCE);
        if (circle.contains(player.position)) {
            circle.setRadius(0.2f);
            circle.setPosition(sprite.getX() + sprite.getOriginX(),sprite.getY()+ sprite.getOriginY());
            //The start length to search from.
            //Creates the queue for the positions transversal.
            PriorityQueue<Node> queue = new PriorityQueue<>();
            BitSet bitSet = new BitSet();
            ArrayList<Node> path = new ArrayList<>();
            //Gets the entity's position.
            entityPos = entity.position;
            //Gets the player's position.
            playerPos = player.position;
            Node goal = Node.vecToNode(playerPos);
            Node start = Node.vecToNode(entityPos);
            Node node = nearestPosToGoal(start,goal);
            node.setIndex(1);
            queue.add(node);
            while (!queue.isEmpty()) {
                Node currentNode = queue.poll();
                if (currentNode.equals(goal)){
                    path.add(currentNode);
                    return path;
                }
                float distance = currentNode.dst(goal);
                Node nextNode = nearestPosToGoal(currentNode,goal);
                nextNode.setIndex(currentNode.index + 1);
                circle.setPosition(nextNode.x, nextNode.y);
                if (universeManager.isCollisionTest(circle)) continue;
                if (!bitSet.get(nextNode.index) && nextNode.dst(goal) < distance){
                    bitSet.set(nextNode.index,true);
                    queue.add(nextNode);
                    path.add(currentNode);
                }
                else {
                    for (Directions directions : DIRECTIONS) {
                        Node neighboringNode = getNodeAtNeighbor(nextNode,directions);
                        circle.setPosition(neighboringNode.x, neighboringNode.y);
                        if (universeManager.isCollisionTest(circle)) continue;

                        if (neighboringNode.equals(goal)){
                            queue.add(neighboringNode);
                            bitSet.set(neighboringNode.index + currentNode.index + directions.ordinal() + 1, true);
                            path.add(currentNode);
                            break;
                        }
                        if (neighboringNode.dst(goal) < distance){
                            if (!bitSet.get(neighboringNode.index)){
                                bitSet.set(neighboringNode.index + currentNode.index + directions.ordinal() + 1, true);
                                queue.add(neighboringNode);
                                path.add(currentNode);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    public ArrayList<Node> pathFindEntity2(Entity entity) {
        if (entity.isLiving) return null;
        Starship player = universeManager.getPlayer();
        Sprite sprite = entity.getSprite();
        //The circle detection range for the path finding.
        Circle circle = new Circle(sprite.getX(), sprite.getY(), MAX_TRANSVERSAL_DISTANCE);
        if (circle.contains(player.position)) {
            circle.setRadius(0.5f);
            circle.setPosition(sprite.getX() + sprite.getOriginX(),sprite.getY()+ sprite.getOriginY());
            //The start length to search from.
            //Gets the entity's position.
            entityPos = entity.position;
            //Gets the player's position.
            playerPos = player.position;
            //Creates the queue for the positions transversal.
            Queue<Node> queue = new ArrayDeque<>();
            ArrayList<Node> path = new ArrayList<>();
            HashSet<Node> visit = new HashSet<>();
            Node goal = Node.vecToNode(playerPos);
            Node start = Node.vecToNode(entityPos);
            queue.add(start);
            while (!queue.isEmpty()) {
                Node node = queue.poll();
                path.add(node);
                float distance = node.dst(goal);
                if (node.equals(goal)) {
                    path.add(goal);
                    return path;
                }
                for (Directions direction: DIRECTIONS) {
                    Node nextNode = getNodeAtNeighbor(node,direction);
                    if (!visit.contains(nextNode) && nextNode.dst(goal) < distance){
                        visit.add(start);
                        queue.add(nextNode);
                    }
                }
            }
            return path;
        }
        return null;
    }


    //Returns the closest node to the goal.
    private Node nearestPosToGoal(Node start,Node goal){
        float distance = start.dst(goal);
        Node check = new Node(start.x,start.y);
        Node newNode = new Node();

        check.set(start.x + Directions.LEFT.value,start.y);
        if (check.dst(goal) < distance  || check.equals(goal)){
            newNode.set(check);
            distance = check.dst(goal);
        }
        check.set(start.x + Directions.RIGHT.value,start.y);
        if (check.dst(goal) < distance  || check.equals(goal)){
            newNode.set(check);
            distance = check.dst(goal);
        }
        check.set(start.x,start.y + Directions.UP.value);
        if (check.dst(goal) < distance || check.equals(goal)){
            newNode.set(check);
            distance = check.dst(goal);
        }
        check.set(start.x,start.y + Directions.DOWN.value);
        if (check.dst(goal) < distance || check.equals(goal)){
            newNode.set(check);
        }
        return newNode;
    }

    private Node getNodeAtNeighbor(Node node,Directions directions){
        if (directions.equals(Directions.UP) || directions.equals(Directions.DOWN)){
            return new Node(node.x,node.y + directions.value);
        }
        if (directions.equals(Directions.LEFT) || directions.equals(Directions.RIGHT)){
            return new Node(node.x + directions.value,node.y);
        }
        return node;
    }




    public static class Node implements Comparable<Node> {
        public int x = 0;
        public int y = 0;
        private int index;
        public Node(int x, int y){
            this.x = x;
            this.y = y;
        }
        public Node(int x, int y,int index){
            this.x = x;
            this.y = y;
            this.index = index;
        }
        public Node(Node node){
            this.x = node.x;
            this.y = node.y;
            this.index = node.index;
        }
        public Node(){

        }
        public void setIndex(int index) {
            this.index = index;
        }
        public int getIndex() {
            return index;
        }


        public Node set(Node node){
            x = node.x;
            y = node.y;
            index = node.getIndex();
            return this;
        }

        public Node set(float x ,float y) {
            this.x = MathUtils.floor(x);
            this.y = MathUtils.floor(y);
            return this;
        }

        public Node set(int x ,int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) return false;
            Node node = (Node) obj;
            return x == node.x && y == node.y;
        }

        public static Node vecToNode(Vector2 vector2){
            return new Node(MathUtils.floor(vector2.x),MathUtils.floor(vector2.y));
        }

        public float dst (Node node) {
            final int x_d = node.x - x;
            final int y_d = node.y - y;
            return (float)Math.sqrt(x_d * x_d + y_d * y_d);
        }

        @Override
        public int hashCode() {
            return x + 31 + y;
        }

        @Override
        public String toString() {
            return x+" : "+y;
        }

        @Override
        public int compareTo(Node node) {
            return index - node.index;
        }
    }
}
