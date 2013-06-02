package com.n3wt0n.spacefight.Entities.Component;

import org.newdawn.slick.Image;

import com.n3wt0n.spacefight.Entities.Pawn;
import com.n3wt0n.spacefight.Util.EntityList;

public class Weapon {
	
	private EntityList entityList;

	private float range = 0f;
	private float rateOfFire = 1f; // Time in seconds between firing a missile
	private float timeSinceFired = 1f;

	private Pawn pawn;

	private Missile missile;
	private float missileScale;
	private float missileVelocity;
	private Image missileImage;

	// min and max power
	private int[] power = new int[2];
	
	private Image image;

	public Weapon(Pawn pawn) {
		this.pawn = pawn;
		
		power[0] = 1;
		power[1] = 2;
		
		range = 10f;
		rateOfFire = 0.3f;
		timeSinceFired = 1f;
		
		missileVelocity = 10f;
		missileScale = 1f;
	}
	
	public Weapon (Pawn pawn, float range, float rateOfFire) {
		this.pawn = pawn;
		
		this.range = range;
		this.rateOfFire = rateOfFire;
	}
	
	public void update(int delta) {
		this.setTimeSinceFired(timeSinceFired + (delta/1000f));
	}

	public void setRange(float range) {
		this.range = range;
	}

	public float getRange() {
		return range;
	}
	
	public float getRateOfFire() {
		return rateOfFire;
	}

	public void setRateOfFire(float rateOfFire) {
		this.rateOfFire = rateOfFire;
	}
	
	public float getTimeSinceFired() {
		return timeSinceFired;
	}

	public void setTimeSinceFired(float timeSinceFired) {
		this.timeSinceFired = timeSinceFired;
	}
	
	public int[] getPower() {
		return power;
	}
	
	public void setPower(int min, int max) {
		power[0] = min;
		power[1] = max;
	}
	
	public void setMinPower(int p) {
		power[0] = p;
	}
	
	public int getMinPower() {
		return power[0];
	}
	
	public void setMaxPower(int p) {
		power[1] = p;
	}
	
	public int getMaxPower() {
		return power[1];
	}

	public float getMissileVelocity() {
		return missileVelocity;
	}

	public void setMissileVelocity(float missileVelocity) {
		this.missileVelocity = missileVelocity;
	}

	public void setMissileImage(Image missileImage) {
		this.missileImage = missileImage;
	}

	public Image getMissileImage() {
		return missileImage;
	}
	
	public float getMissileScale() {
		return missileScale;
	}

	public void setMissileScale(float missileScale) {
		this.missileScale = missileScale;
	}
	
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	
	public Pawn getPawn() {
		return pawn;
	}

	public void setPawn(Pawn pawn) {
		this.pawn = pawn;
	}

	public Missile getMissile() {
		return missile;
	}

	public void setMissile(Missile missile) {
		this.missile = missile;
	}
	
	public boolean canFire() {
		return (this.getRateOfFire() - this.getTimeSinceFired() <= 0) ? true : false;
	}

	public void setEntityList(EntityList entityList) {
		this.entityList = entityList;
	}

	public EntityList getEntityList() {
		return entityList;
	}
	
	public Missile fireAt(float x, float y, float angleDegrees) {
		missile = new Missile(x, y, (int)(getMissileImage().getWidth()*getMissileScale()), (int)(getMissileImage().getHeight()*getMissileScale()), getMissileImage().getScaledCopy(getMissileScale()), "bullet", this);
		missile.setTeam(this.getPawn().getTeam());
		missile.setRotation(angleDegrees);
		missile.setName(this.getPawn().getName() + "'s bullet");
		missile.setVelocity(getMissileVelocity());
		missile.setTiledMap(this.pawn.getTiledMap());
		missile.setEntityList(getEntityList());
		this.setTimeSinceFired(0f);
		getEntityList().addToBuffer(missile);
		return missile;
	}
	
}
