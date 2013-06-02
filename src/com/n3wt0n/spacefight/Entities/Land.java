package com.n3wt0n.spacefight.Entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Land {
	
	private int x, y;
	private Image image;

	public Land(int x, int y, String name) throws SlickException {
		this.x = x;
		this.y = y;
		this.image = new Image("media/gfx/001_bush_01.png");
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
