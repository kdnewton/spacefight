package com.n3wt0n.spacefight.Map;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.geom.Circle;

@SuppressWarnings("serial")
public class Node extends Circle {
	
	private boolean isValid = false;
	
	private List<Node> neighbors = new LinkedList<Node>();

	public Node(float centerPointX, float centerPointY, float radius) {
		super(centerPointX, centerPointY, radius);
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setNeighbors(List<Node> neighbors) {
		this.neighbors = neighbors;
	}

	public List<Node> getNeighbors() {
		return neighbors;
	}
}
