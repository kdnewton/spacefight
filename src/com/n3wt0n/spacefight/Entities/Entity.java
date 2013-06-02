package com.n3wt0n.spacefight.Entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import com.n3wt0n.spacefight.Util.EntityList;

public abstract class Entity extends SimpleEntity {
	
	private TiledMap tiledMap;
	private EntityList entityList;
	private boolean isAlive;

	protected float scale;
	
	// Quickest this is allowed to move.
	// Also useful for having a value to reset the pawn's velocity to.
	protected float maxVelocity;
	// The current velocity (may be different than maxVelocity due to being sped up or slowed down)
	protected float velocity;
	
	/**
	 * Create an Entity. Extend it for use as a Lamp, NPC, or Player, etc.
	 * 
	 * @param x
	 *            Initial location in x-plane.
	 * @param y
	 *            Initial location in y-plane.
	 * @param width
	 *            Width to assign the physical body.
	 * @param height
	 *            Height to assign the physical body.
	 * @param mass
	 *            Mass to assign the physical body.
	 * @param name
	 *            Name of the Entity.
	 */
	public Entity(float x, float y, int width, int height, String name) {
		super(x, y, width, height, name);
		isAlive = true;
	}

	/**
	 * Update the variables of the Entity each step.
	 * 
	 * @param delta
	 *            Time passed since last update.
	 * @throws SlickException
	 */
	public void update(int delta) throws SlickException {
	}

	/**
	 * Render the Entity.
	 * 
	 * @param g
	 *            Java Graphics.
	 * @throws SlickException
	 */
	public abstract void render(Graphics g)
			throws SlickException;

	/**
	 * Called before Update.
	 * 
	 * @param delta
	 *            Time passed since last update.
	 */
	public void preUpdate(int delta) {
	}
	
	public void die() {
		if (isAlive) {
			// Cycle entities, anyone who had me as a target, nullify their target
			for (Entity e : getEntityList().getList()) {
				// Check only the pawns
				if (e.getClass().getName().equals(Pawn.class.getName())) {
					Pawn p = (Pawn)e;
					if (p.getTargetPawn() != null && p.getTargetPawn().equals(this)) {
						p.setTargetPawn(null);
					}
				}
			}
			isAlive = false;
			getEntityList().addToGraveyard(this);
		}
	}

	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public float getMaxVelocity() {
		return maxVelocity;
	}
	
	public void setMaxVelocity(float maxVelocity) {
		this.maxVelocity = maxVelocity;
	}
	
	public float getVelocity() {
		return velocity;
	}
	
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}

	public TiledMap getTiledMap() {
		return tiledMap;
	}

	public void setEntityList(EntityList entityList) {
		this.entityList = entityList;
	}

	public EntityList getEntityList() {
		return entityList;
	}
	
	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

}
