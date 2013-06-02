package com.n3wt0n.spacefight.Entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public abstract class SimpleEntity {

	// Rotation of image can be different than angle of direction.
	// Think of it as soldier strafing west while facing north.
	private float rotation; // the rotation of the image. setRotation(0) for upright.

	protected Image image;
	
	private int width, height;
	
	private Color color;

	// For the player position on the screen
	private int screenx, screeny;
	
	protected float x, y;
	
	protected float currentHealth, maxHealth;

	// Entity velocity and direction
	private float velocity;

	private boolean moving = false;

	private String name;

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
	public SimpleEntity(float x, float y, int width, int height, String name) {
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.name = name;
		
		setMaxHealth(100);
	}

	/**
	 * Update the variables of the Entity each step.
	 * 
	 * @param delta
	 *            Time passed since last update.
	 * @throws SlickException
	 */
	public abstract void update(int delta) throws SlickException;

	/**
	 * Render the Entity.
	 * 
	 * @param g
	 *            Java Graphics.
	 * @throws SlickException
	 */
	public abstract void render(Graphics g) throws SlickException;

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getRotation() {
		return rotation;
	}

	/**
	 * Not yet used. Will eventually check for intersection with the body of
	 * another Entity.
	 * 
	 * @param entity
	 *            The Entity to test the collision with.
	 * @return False.
	 */
	public boolean collidedWith(SimpleEntity entity) {
		return this.getRectangle().intersects(entity.getRectangle()) ? true : false;
	}

	/**
	 * Get the name of the Entity.
	 * 
	 * @return The Entity name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the Entity.
	 * 
	 * @param name
	 *            Entity name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the moving flag to True or False.
	 * 
	 * @param b
	 *            True or False if the Entity is moving or not.
	 */
	public void setMoving(boolean b) {
		moving = b;
	}

	/**
	 * Check to see if the Entity is moving.
	 * 
	 * @return True if moving. False if resting.
	 */
	public boolean isMoving() {
		return moving;
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
	
	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float[] getLocation() {
		float[] location = new float[2];
		location[0] = this.x;
		location[1] = this.y;
		return location;
	}

	/**
	 * Set the visual position of the Entity within the GameContainer.
	 * 
	 * @param x
	 *            Location along x-plane.
	 * @param y
	 *            Location along y-plane.
	 */
	public void setVisualLocation(float x, float y) {
		this.screenx = (int) x;
		this.screeny = (int) y;
	}

	/**
	 * Set the visual position of the Entity along the x-plane.
	 * 
	 * @param x
	 *            Location along x-plane.
	 */
	public void setVisualX(float x) {
		this.screenx = (int) x;
	}

	/**
	 * Set the visual position of the Entity along the y-plane.
	 * 
	 * @param y
	 *            Location along y-plane.
	 */
	public void setVisualY(float y) {
		this.screeny = (int) y;
	}

	/**
	 * Get the visual position of the Entity in the x-plane.
	 * 
	 * @return X location of Entity on the screen.
	 */
	public float getVisualX() {
		return screenx;
	}

	/**
	 * Get the visual position of the Entity in the y-plane.
	 * 
	 * @return Y location of Entity on the screen.
	 */
	public float getVisualY() {
		return screeny;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Get the velocity of the Entity.
	 * 
	 * @return Entity velocity.
	 */
	public float getVelocity() {
		return this.velocity;
	}

	/**
	 * Set the Entity velocity.
	 * 
	 * @param velocity Velocity to set.
	 */
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	/**
	 * Get the Entity width.
	 * 
	 * @return Entity width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Set the Entity width.
	 * 
	 * @param width
	 *            Width to set.
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Get the Entity height.
	 * 
	 * @return Entity height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Set the Entity height.
	 * 
	 * @param height
	 *            Height to set.
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * Get the SpriteSheet
	 * @return sheet
	 */
	public Image getImage() {
		return image;
	}
	/**
	 * Set the SpriteSheet
	 * @param image
	 */
	public void setImage(Image image) {
		this.image = image;
	}
	
	/**
	 * get the health value
	 * @return health
	 */
	public float getCurrentHealth() {
		return currentHealth;
	}
	/**
	 * Set the health value
	 * @param health
	 */
	public void setCurrentHealth(float health) {
		this.currentHealth = health;
	}
	
	public float getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(float maxHealth) {
		this.maxHealth = maxHealth;
	}

	/**
	 * Get Rectangle shape for collision detection
	 * @return rectangle
	 */
	public Rectangle getRectangle() {
		return new Rectangle(
				this.getX() - (this.getWidth() / 2),
				this.getY() - (this.getHeight() / 2),
				this.getWidth(),
				this.getHeight()
		);
	}

}
