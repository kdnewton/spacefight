package com.n3wt0n.spacefight.Entities.Component;

public class Transportation {
	
	private float speed = 0f;
	
	public Transportation() {
		
	}
	
	public Transportation (float speed) {
		this.speed = speed;
	}

	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
