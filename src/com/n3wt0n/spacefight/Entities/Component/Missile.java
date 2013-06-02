package com.n3wt0n.spacefight.Entities.Component;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.n3wt0n.spacefight.Entities.Entity;
import com.n3wt0n.spacefight.Entities.Pawn;

public class Missile extends Entity {
	
	private Weapon weapon;
	private float velocity;
	
	private int team;
	
	public Missile(float x, float y, int width, int height, Image image, String name, Weapon weapon) {
		super(x, y, width, height, name);
		this.weapon = weapon;
		this.image = image;
		this.velocity = 10;
	}

	@Override
	public void update(int delta) throws SlickException {
		setX((float) (getX() + velocity * Math.cos(Math.toRadians(getRotation()))));
		setY((float) (getY() + velocity * Math.sin(Math.toRadians(getRotation()))));
		
		for (Entity e : this.getWeapon().getPawn().getEntityList().getList()) {
			// Iterate the pawns, check if I collide with them
			if (e.getClass().getName().equals(Pawn.class.getName())) {
				Pawn p = (Pawn)e;
				if (p.getTeam() != this.team && this.collidedWith(p)) {
//					float targetHealth = hit(p);
					hit(p);
//					System.out.println (p.getName() + " health: " + targetHealth + " / " + p.getMaxHealth());
				}
			}
		}
		
		// Check if the entity is outside of the map boundaries. Remove it if it is.
		if (getX() < 0 || getY() < 0 ||
				getX() > (getTiledMap().getWidth()*getTiledMap().getTileWidth())-1 ||
				getY() > (getTiledMap().getHeight()*getTiledMap().getTileHeight())-1) {
			die();
			return;
		}
		
		try {
			// Check for collision with a wall
			Image image = this.getTiledMap().getTileImage((int)(getX()/this.getTiledMap().getTileWidth()), (int)(getY()/this.getTiledMap().getTileHeight()), this.getTiledMap().getLayerIndex("WALLS"));
			if (image != null) {
				die();
				return;
			}
		}
		catch (Exception e) {
			System.out.println ("Missile exception - update: " + e);
		}
	}

	@Override
	public void render(Graphics g) throws SlickException {
		try {
			image.setRotation(getRotation());
			g.drawImage(image, getX()-(getWidth()/2), getY()-(getHeight()/2));
		}
		catch (Exception e) {
			System.out.println ("Missile Render Exception: " + e);
		}
		
		// Bounding box
//		g.setColor(new Color(0, 255, 0));
//		g.drawOval(
//				getRectangle().getX(),
//				getRectangle().getY(),
//				getRectangle().getWidth(),
//				getRectangle().getHeight()
//		);
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public float getVelocity() {
		return velocity;
	}

	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}
	
	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}
	
	public float hit(Entity target) {
		float targetHealth = target.getCurrentHealth();
		targetHealth -= weapon.getMaxPower();
		target.setCurrentHealth(targetHealth);
		// TODO: If the item is a laser (can pass through enemies) then don't die() here
		die();
		return targetHealth;
	}

}
