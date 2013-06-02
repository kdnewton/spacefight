package com.n3wt0n.spacefight.Entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import com.n3wt0n.spacefight.Entities.Component.Shield;
import com.n3wt0n.spacefight.Entities.Component.Transportation;
import com.n3wt0n.spacefight.Entities.Component.Weapon;
import com.n3wt0n.spacefight.Util.AccessibleAreaMap;

public class Pawn extends Entity {
	
	private boolean hasFocus;
	private int team, playerTeam;
	private int bounds;
	private float startingX, startingY;
	private float destX, destY;
	private float potentialDestX, potentialDestY;
	private Pawn targetPawn;
	
	private AccessibleAreaMap accessibleAreaMap;

	// Distance between starting position and final destination
	private float distToDest;
	private boolean reachedDestination;
	private boolean isFollowingOrders;

	private int turnLimit;
	private float range;
	private float angleRadian;
	private float angleDegrees;
	
	// TODO: get rid of transportation... velocity is calculated by range and amount of time a turn lasts
	// Components that make up a pawn.
	// Modifiers multiply into the component to enhance it.
	private Transportation transportation;
//	private float transportationModifier;
	private Weapon weapon;
//	private float weaponModifier;
	private Shield shield;
//	private float shieldModifier;
	
	private Intelligence intelligence;

	public Pawn(float x, float y, int width, int height, String name, int team, float rotation, float health, TiledMap map) throws SlickException {
		super(x, y, width, height, name);
		this.team = team;
		image = new Image("media/gfx/ship_001_32x32.png");
		this.currentHealth = health;
		this.setTiledMap(map);
		
		weapon = new Weapon(this);
		weapon.setImage(image);
		weapon.setPower(1, 10);
	}

	public void init() {
		setWidth(image.getWidth());
		setHeight(image.getHeight());
		
		this.setAlive(true);
		destX = -1;
		destY = -1;
		if (range == 0f)
			range = 200f;
		maxVelocity = 1f;
		
		// Bounds is defined as the square root of the image width times height
		// That way the average clickable area will be close to the pawn
		bounds = (int) Math.sqrt(getWidth()*getHeight());
		
		System.out.println ("Init pawn '" + this.getName() + "' (" + this.getX() + "," + this.getY() + ")");
		
		// Calculate maximum velocity (parent Entity value)
		maxVelocity = range / turnLimit;
		velocity = maxVelocity;
		
		reset();
	}
	
	public void reset() {
		distToDest = 0;
		reachedDestination = false;
		isFollowingOrders = false;
		setStartingLocation(getX(), getY());
		if (getTeam() == getPlayerTeam()) {
			accessibleAreaMap = new AccessibleAreaMap(getTiledMap(), x, y, range);
			accessibleAreaMap.setWallLayer("WALLS");
			accessibleAreaMap.init();
		}
	}

	@Override
	public void render(Graphics g) {
		if (getTeam() == getPlayerTeam() && !isFollowingOrders && hasFocus) {
			accessibleAreaMap.render(g);
		}
		image.setRotation(this.getRotation());
		g.drawImage(image, x-(getWidth()/2), y-(getHeight()/2));
		
		// TODO: Provide an "Always show orders" option, and bypass the "isFollowingOrders" check below when used
		
		if (getTeam() != getPlayerTeam() || isFollowingOrders) {
			return;
		}
		
		// If the pawn hasFocus...
		// Highlight the pawn (with a red square around it)
		if (hasFocus) {
			g.setColor(new Color(255,32,32));
			g.drawRect(x-(getWidth()/2), y-(getHeight()/2), getWidth()-1, getHeight()-1);
			drawPotentialDestination(g);
			
			drawRange(g);
		}
		
		// If the pawn has a destination, display that destination.
		if (hasDestination()) {
			g.setColor(new Color(75,75,75));
			g.drawLine(getX(), getY(), getDestX(), getDestY());
			g.setColor(new Color(100,100,100));
			g.drawRect(getDestX()-(getWidth()/2), getDestY()-(getHeight()/2), getWidth(), getHeight());
		}
		
		if (hasTarget()) {
			g.setColor(Color.yellow);
			g.drawLine(getDestX(), getDestY(), targetPawn.getX(), targetPawn.getY());
		}
	}
	
	public void drawRange(Graphics g) {
		g.setColor(new Color(32,255,32));
		g.drawOval(x-range, y-range, range*2, range*2);
		//Path p = new Path(getTiledMap(), x, y, range);
	}

	/**
	 * Follows the orders given to the pawn and updates their position/action/state/etc
	 */
	public void update(int delta, int turnTime) {
		
		// TODO: If my target died last turn, pick a new one and start shooting him
		
		if (getCurrentHealth() <= 0 && this.isAlive()) {
			this.die();
			return;
		}
		
		// We can still fire our weapon even if we've reached our destination
		if (weapon.canFire() && targetPawn != null) {
			fireAt(targetPawn);
		}
		weapon.update(delta);
		
		if (reachedDestination || !this.isAlive())
			return;
		
		float percentOfTimePassed = (float) turnTime / (float) turnLimit;
		float distanceTraveled = range * percentOfTimePassed;
		
		// If you've reached your destination then stop
		if (distanceTraveled >= distToDest) {
			setX(getPotentialDestX());
			setY(getPotentialDestY());
			reachedDestination = true;
			isFollowingOrders = false;
			return;
		}
		
		// Calculate new position with angle and velocity
		// setX for the camera but use startingX for a percentage-based location
		setX((float) (getStartingX() + distanceTraveled * Math.cos(angleRadian)));
		setY((float) (getStartingY() + distanceTraveled * Math.sin(angleRadian)));
	}

	public void drawPotentialDestination(Graphics g) {
		// Ideally we would draw an arrow here.
		// Also, we'll draw the actual potential location (the one based on the range of the pawn).
		// Can still trace to the mouse location, but the actual potential location should be clearly shown.
		g.setColor(new Color(255,32,32));
		g.drawLine(getX(), getY(), getPotentialDestX(), getPotentialDestY());
	}

	public void limitRange() {
		float diffX = getPotentialDestX() - getX();
		float diffY = getPotentialDestY() - getY();
		distToDest = (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));

		if (distToDest > range) {
			// Calculate angle between position x,y and destination x,y
			angleRadian = (float) Math.atan2(diffY, diffX);
			// Calculate potential destination based on angle and range
			float pX = (float) (getX() + range * Math.cos(angleRadian));
			float pY = (float) (getY() + range * Math.sin(angleRadian));
			setPotentialDestination(pX, pY);
		}
	}
	
	public boolean hasFocus() {
		return hasFocus;
	}

	public void setHasFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
	}

	/**
	 * Should be true when the pawn has been assigned a destination for the turn.
	 * @return
	 */
	public boolean hasDestination() {
		return (this.getDestX() >= 0 && this.getDestY() >= 0);
	}

	/**
	 * Should be true when the pawn has been assigned a target for the turn.
	 * @return
	 */
	public boolean hasTarget() {
		return (this.getTargetPawn() != null);
	}
	
	public boolean hasOrders() {
		// when pawn hasDestination and hasTarget, then pawn hasOrders
		return (hasDestination() && hasTarget());
	}
	
	public boolean isFollowingOrders() {
		return isFollowingOrders;
	}

	public void setFollowingOrders(boolean isFollowingOrders) {
		this.isFollowingOrders = isFollowingOrders;
	}
	
	public int getTeam() {
		return team;
	}
	
	public void setTeam(int team) {
		this.team = team;
	}
	
	public int getPlayerTeam() {
		return playerTeam;
	}

	public void setPlayerTeam(int playerTeam) {
		this.playerTeam = playerTeam;
	}
	
	public float getStartingX() {
		return startingX;
	}
	
	public void setStartingX(float startingX) {
		this.startingX = startingX;
	}
	
	public float getStartingY() {
		return startingY;
	}
	
	public void setStartingY(float startingY) {
		this.startingY = startingY;
	}
	
	public void setStartingLocation(float startingX, float startingY) {
		this.startingX = startingX;
		this.startingY = startingY;
	}

	public float getDestX() {
		return destX;
	}
	
	public void setDestX(float destX) {
		this.destX = destX;
	}
	
	public float getDestY() {
		return destY;
	}
	
	public void setDestY(float destY) {
		this.destY = destY;
	}
	
	public void setDestination(float destX, float destY) {
		this.destX = destX;
		this.destY = destY;
	}

	public float getPotentialDestX() {
		return potentialDestX;
	}
	
	public float getPotentialDestY() {
		return potentialDestY;
	}
	
	public void setPotentialDestination(float destX, float destY) {
		this.potentialDestX = destX;
		this.potentialDestY = destY;
	}
	
	public void setAngleRadian(float angleRadian) {
		this.angleRadian = angleRadian;
	}
	
	public float getAngleRadian() {
		return angleRadian;
	}
	
	public void setAngleDegrees(float angleDegrees) {
		this.angleDegrees = angleDegrees;
	}
	
	public float getAngleDegrees() {
		return angleDegrees;
	}
	
	/**
	 * For the point x,y check if it's within bounds of the pawn
	 * @param mouseX the x point to check
	 * @param mouseY the y point to check
	 */
	public boolean isAt(int mouseX, int mouseY) {
		float offX = this.x;
		float offY = this.y;
		if (Math.abs(offX-mouseX) < (bounds/2) && Math.abs(offY-mouseY) < (bounds/2)) {
			return true;
		}
		return false;
	}
	
	public void setRange(float range) {
		this.range = range;
	}
	
	public float getRange() {
		return range;
	}
	
	public void setTurnLimit(int turnLimit) {
		this.turnLimit = turnLimit;
	}

	public void setTransportation(Transportation transportation) {
		this.transportation = transportation;
	}

	public Transportation getSpeed() {
		return transportation;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public void setShield(Shield shield) {
		this.shield = shield;
	}

	public Shield getShield() {
		return shield;
	}
	
	public Pawn getTargetPawn() {
		return targetPawn;
	}
	
	public void setTargetPawn(Pawn targetPawn) {
		this.targetPawn = targetPawn;
	}
	
	public void makeIntelligent(boolean intelligent) {
		if (intelligent && this.getEntityList() != null) {
			intelligence = new Intelligence(this, this.getEntityList());
		}
		else {
			intelligence = null;
		}
	}
	
	/**
	 * When Pawn is computer controlled, this activates the Pawn's intelligence
	 */
	public void pickMove() {
		try {
			intelligence.go();
		}
		catch (Exception e) {
			System.out.println ("Exception: " + e);
		}
	}
	
	public void fireAt(Pawn target) {
		try {
			float angleDegrees = (float) Math.toDegrees(Math.atan2(target.getY() - this.getY(), target.getX() - this.getX()));
			weapon.fireAt(this.getX(), this.getY(), angleDegrees);
		} catch (Exception e) {
			System.out.println ("fireAt exception: " + e);
		}
	}
}
