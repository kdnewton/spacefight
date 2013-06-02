package com.n3wt0n.spacefight.Map;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

@SuppressWarnings("serial")
public class Wall extends Rectangle {
	
	public final int TOP = 0, RIGHT = 1, BOTTOM = 2, LEFT = 3,
		TOPLEFT = 4, TOPRIGHT = 5, BOTTOMRIGHT = 6, BOTTOMLEFT = 7;
	
	// top, right, bottom, left
	private int[] visibleEdge = {1, 1, 1, 1};
	
	private Node[] nodes = new Node[4];

	public Wall(float x, float y, float width, float height) {
		super(x, y, width, height);
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new Node(super.getPoint(i)[0], super.getPoint(i)[1], 0);
			nodes[i].setValid(true);
//			System.out.println ("Node: " + nodes[i].getCenterX() + "," + nodes[i].getCenterY());
		}
	}
	
	public Node[] getNodes() {
		return nodes;
	}

	public void setNodes(Node[] nodes) {
		this.nodes = nodes;
	}
	
	public void makeEdgeInvisible(int i) {
		visibleEdge[i] = 0;
	}
	
	public void validateNodes(TiledMap map, String wallLayer) {
//		System.out.println ("Validating node");
		if (hasNeighborTo(TOPLEFT, map, wallLayer)) {
			nodes[0].setValid(false);
		}
		if (hasNeighborTo(TOP, map, wallLayer)) {
			nodes[0].setValid(false);
			nodes[1].setValid(false);
		}
		if (hasNeighborTo(TOPRIGHT, map, wallLayer)) {
			nodes[1].setValid(false);
		}
		if (hasNeighborTo(RIGHT, map, wallLayer)) {
			nodes[1].setValid(false);
			nodes[2].setValid(false);
		}
		if (hasNeighborTo(BOTTOMRIGHT, map, wallLayer)) {
			nodes[2].setValid(false);
		}
		if (hasNeighborTo(BOTTOM, map, wallLayer)) {
			nodes[2].setValid(false);
			nodes[3].setValid(false);
		}
		if (hasNeighborTo(BOTTOMLEFT, map, wallLayer)) {
			nodes[3].setValid(false);
		}
		if (hasNeighborTo(LEFT, map, wallLayer)) {
			nodes[0].setValid(false);
			nodes[3].setValid(false);
		}
	}
	
	public void checkNeighbors(TiledMap map, String wallLayer) {
		if (hasNeighborTo(TOP, map, wallLayer)) {
			makeEdgeInvisible(TOP);
		}
		if (hasNeighborTo(RIGHT, map, wallLayer)) {
			makeEdgeInvisible(RIGHT);
		}
		if (hasNeighborTo(BOTTOM, map, wallLayer)) {
			makeEdgeInvisible(BOTTOM);
		}
		if (hasNeighborTo(LEFT, map, wallLayer)) {
			makeEdgeInvisible(LEFT);
		}
	}
	
	public boolean hasNeighborTo(int direction, TiledMap map, String wallLayer) {
		int x = (int)this.x/map.getTileWidth();
		int y = (int)this.y/map.getTileHeight();
		
		switch(direction) {
		case TOP:
			y = ((y-1) < 0) ? 0 : (y-1);
			break;
		case TOPLEFT:
			x = ((x-1) < 0) ? 0 : (x-1);
			y = ((y-1) < 0) ? 0 : (y-1);
			break;
		case RIGHT:
			x = ((x+1) >= map.getWidth()) ? map.getWidth()-1 : (x+1);
			break;
		case TOPRIGHT:
			x = ((x+1) >= map.getWidth()) ? map.getWidth()-1 : (x+1);
			y = ((y-1) < 0) ? 0 : (y-1);
			break;
		case BOTTOM:
			y = ((y+1) >= map.getHeight()) ? map.getHeight()-1 : (y+1);
			break;
		case BOTTOMRIGHT:
			x = ((x+1) >= map.getWidth()) ? map.getWidth()-1 : (x+1);
			y = ((y+1) >= map.getHeight()) ? map.getHeight()-1 : (y+1);
			break;
		case LEFT:
			x = ((x-1) < 0) ? 0 : (x-1);
			break;
		case BOTTOMLEFT:
			x = ((x-1) < 0) ? 0 : (x-1);
			y = ((y+1) >= map.getHeight()) ? map.getHeight()-1 : (y+1);
			break;
		}
		int id = map.getTileId(x, y, map.getLayerIndex(wallLayer));
		if (id > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isVisible(int direction) {
		return (visibleEdge[direction] == 1) ? true : false;
	}
}
