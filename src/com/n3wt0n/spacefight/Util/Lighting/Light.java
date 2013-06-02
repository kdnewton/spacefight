package com.n3wt0n.spacefight.Util.Lighting;

import org.newdawn.slick.Color;

/*
 * Based on Kevin Glass's code from http://slick.javaunlimited.net/viewtopic.php?p=16285#16285
 */

public class Light {
	
	private float x, y;
	private float radius;
	private Color color;
	
	public Light(float x, float y, float radius) {
		this.setX(x);
		this.setY(y);
		this.setRadius(radius);
		this.color = Color.white; // white light by default
	}
	
	public Light(float x, float y, float radius, Color color) {
		this.setX(x);
		this.setY(y);
		this.setRadius(radius);
		this.setColor(color);
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRadius() {
		return radius;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
}
