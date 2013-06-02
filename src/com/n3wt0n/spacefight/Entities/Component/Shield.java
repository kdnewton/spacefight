package com.n3wt0n.spacefight.Entities.Component;

public class Shield {
	
	// How much of the weapon damage the shield takes
	private float effectiveness = 0f;
	// How much longer the shield will last
	private float power = 0f;
	
	public Shield() {
		
	}
	
	public Shield(float effectiveness, float power) {
		this.effectiveness = effectiveness;
		this.power = power;
	}
	
	public float getEffectiveness() {
		return effectiveness;
	}

	public void setEffectiveness(float effectiveness) {
		this.effectiveness = effectiveness;
	}

	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
	}
	
}
