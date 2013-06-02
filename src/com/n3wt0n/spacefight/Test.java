package com.n3wt0n.spacefight;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import com.n3wt0n.spacefight.Entities.Entity;
import com.n3wt0n.spacefight.Entities.Pawn;
import com.n3wt0n.spacefight.Entities.Component.Missile;
import com.n3wt0n.spacefight.Map.Map;
import com.n3wt0n.spacefight.Util.Camera;
import com.n3wt0n.spacefight.Util.EntityList;

public class Test extends BasicGame {
	private boolean debugMode = false;
	private String dString = "";
	
	private Camera camera;
	
	private Map map;

	private EntityList entityList;
	private Pawn selectedPawn;
	private boolean gamePaused = false;
	private boolean readyToGo = false;

	private static final int PLAYER_TEAM = 1;

	private int currentState = -1;
	private static final int PICK_ENEMY_MOVES = 0;
	private static final int DESTINATION_STATE = 1;
	private static final int TARGET_STATE = 2;
	private static final int REVIEW_STATE = 3;
	private static final int GO_STATE = 4;
	
	private Input input;
	private int mouseX, mouseY;
	private int controlDelta = 10;
	private int myDelta;
	
	private int turnLimit = 3; // In seconds, how long a turn lasts.
	private int turnTime = 0; // current point in the turn.
	private int totalGoTime = 0;
	private String totalGoString = "00:00:00:00";

	public Test() {
		super("Space Fight!");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		
		turnLimit *= 1000; // convert to seconds
		
		gc.setTargetFrameRate(30);
		gc.setShowFPS(false);
		
		map = new Map(new TiledMap("media/map/map_000.tmx"));
		map.buildMap();
		
		entityList = new EntityList();
		// Give the player a few pawns to control
		Image image = new Image("media/gfx/ship_001_32x32.png");
		int team = 1;
		Pawn p;
		entityList.add(new Pawn(gc.getWidth()*0.33f, gc.getHeight()*0.85f, image.getWidth(), image.getHeight(), "Bob", team, 0, 100f, map.getTiledMap()));
		p = (Pawn)entityList.getLast();
		p.setImage(image);
		p.setEntityList(entityList);
		p.setPlayerTeam(PLAYER_TEAM);
		p.getWeapon().setRateOfFire(0.4f);
		p.getWeapon().setMissileVelocity(8f);
		p.getWeapon().setMissileImage(new Image("media/gfx/001_bush_01.png"));
		p.getWeapon().setMissileScale(0.33f);
		p.getWeapon().setEntityList(entityList);
		
//		pl.add(new Pawn(gc.getWidth()*0.50f, gc.getHeight()*0.85f, "Charlie", team, 0));
//		p = (Pawn)pl.getLast();
//		p.setSpriteSheet(new SpriteSheet(image, 1, 1));
//		p.setEntityList(pl);
//		p.setPlayerTeam(PLAYER_TEAM);
//		
//		pl.add(new Pawn(gc.getWidth()*0.66f, gc.getHeight()*0.85f, "Doug", team, 0));
//		p = (Pawn)pl.getLast();
//		p.setSpriteSheet(new SpriteSheet(image, 1, 1));
//		p.setEntityList(pl);
//		p.setPlayerTeam(PLAYER_TEAM);
		
		// And now create a couple opponents
		image = new Image("media/gfx/ship_002_32x32.png");
		team = 2;
		entityList.add(new Pawn(gc.getWidth()*0.33f, gc.getHeight()*0.15f, image.getWidth(), image.getHeight(), "Heckler", team, 180, 100f, map.getTiledMap()));
		p = (Pawn)entityList.getLast();
		p.setImage(image);
		p.setEntityList(entityList);
		p.makeIntelligent(true);
		p.setPlayerTeam(PLAYER_TEAM);
		p.setRange(100f);
		p.getWeapon().setRateOfFire(0.50f);
		p.getWeapon().setMissileVelocity(10);
		p.getWeapon().setMissileImage(new Image("media/gfx/000_pawn_01.png"));
		p.getWeapon().setMissileScale(0.50f);
		p.getWeapon().setEntityList(entityList);
		
		entityList.add(new Pawn(gc.getWidth()*0.66f, gc.getHeight()*0.15f, image.getWidth(), image.getHeight(), "Koch", team, 180, 100, map.getTiledMap()));
		p = (Pawn)entityList.getLast();
		p.setImage(image);
		p.setEntityList(entityList);
		p.makeIntelligent(true);
		p.setPlayerTeam(PLAYER_TEAM);
		p.setRange(50f);
		p.getWeapon().setRateOfFire(0.60f);
		p.getWeapon().setMissileVelocity(15);
		p.getWeapon().setMissileImage(new Image("media/gfx/000_pawn_02.png"));
		p.getWeapon().setMissileScale(0.50f);
		p.getWeapon().setEntityList(entityList);
		
		for (Entity e : entityList.getList()) {
			if (e.getClass().getName().equals(Pawn.class.getName())) {
				p = (Pawn)e;
				// Set rules here. maxVelocity, range, etc. before pawn is initialized
				p.setTurnLimit(turnLimit);
				p.init();
				System.out.println ("Initialising pawn " + p.getName());
			}
		}
		
		camera = new Camera(gc, 7f, map);
		camera.setEntityList(entityList);
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		// Do not normalise input.
		// This is mouse-driven input so read whenever input occurs.

		input = gc.getInput();

		if (input.isKeyPressed(Input.KEY_P)) {
			gamePaused = !gamePaused;
		}

		if (gamePaused) {
			return;
		}
		
		// Mouse Inputs (shifted based on camera position)
		mouseX = input.getMouseX()-camera.getX();
		mouseY = input.getMouseY()-camera.getY();
		
		// Keyboard Inputs
		if (input.isKeyDown(Input.KEY_LEFT)) {
			dPrint ("Key down: Left");
			camera.scrollLeft();
		}
		if (input.isKeyDown(Input.KEY_UP)) {
			dPrint ("Key down: Up");
			camera.scrollUp();
		}
		if (input.isKeyDown(Input.KEY_RIGHT)) {
			dPrint ("Key down: Right");
			camera.scrollRight();
		}
		if (input.isKeyDown(Input.KEY_DOWN)) {
			dPrint ("Key down: Down");
			camera.scrollDown();
		}
		
		switch (currentState) {
		
		case PICK_ENEMY_MOVES:
			dPrint ("Enemy Picks Moves State");
			pickEnemyMoves();
			break;

		case DESTINATION_STATE:
			dPrint ("Destination State");
			updateDestinationState(delta);
			break;

		case TARGET_STATE:
			dPrint ("Target State");
			updateTargetState(delta);
			break;

		case REVIEW_STATE:
			dPrint ("Review state");
			updateReviewState(delta);
			break;

		case GO_STATE:
			dPrint ("Go state");
			updateGoState(delta);
			break;

		default:
			// Falls into this state the first time the loop is run
			// So start by picking enemy moves
			currentState = PICK_ENEMY_MOVES;
			break;
		}
		
		// Now that the update is over...
		// Move the new Pawns and Missiles in the buffer list over to the main entity list
		// and clean up the graveyard
		entityList.cleanup();
		
		camera.update();
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		camera.render(gc, g, totalGoString);
		
		if (debugMode) {
			g.drawString("State: " + currentState, 20, (gc.getHeight()-40));
			g.drawString("Pawn: " + (selectedPawn != null ? selectedPawn.getName() : ""), 20, (gc.getHeight()-60));
		}
	}
	
	/**
	 * Grants focus on the pawn that was clicked on.
	 * When setting the pawn to null the focus is cleared.
	 * @param pawn
	 */
	public void selectPawn(Pawn pawn) {
		if (pawn == null) {
			selectedPawn = null;
			return;
		}
		if (selectedPawn != null) {
			if (selectedPawn.equals(pawn)) {
				return;
			}
			// Mark previously selected pawn as no longer selected / in focus.
			selectedPawn.setHasFocus(false);
		}
		pawn.setHasFocus(true);
		selectedPawn = pawn;
		dPrint ("Pawn '" + selectedPawn.getName() + "' has focus: " + selectedPawn.hasFocus());
	}
	
	public void deselectPawn() {
		if (selectedPawn != null) {
			dPrint ("Pawn '" + selectedPawn.getName() + "' has focus: " + selectedPawn.hasFocus());
			selectedPawn.setHasFocus(false);
			selectPawn(null);
		}
	}
	
	public void dPrint(String s) {
		if (debugMode) {
			// Reduce the amount of dPrint output
			if (!dString.equalsIgnoreCase(s)) {
				dString = s;
				System.out.println (dString);
			}
		}
	}
	
	public void updateDestinationState(int delta) {
		// Track the position of the mouse, checking potential destination x,y
		selectedPawn.setPotentialDestination(mouseX, mouseY);
		// If the potential destination falls outside the pawn's range, cut back
		// the position to comply with the range.
		selectedPawn.limitRange();
		
		if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
			// Right click backs out, reverts to the previous state.
			currentState = REVIEW_STATE;
			selectedPawn.setDestination(-1, -1);
			deselectPawn();
		}
		else if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			boolean swappedPawns = false;
			// Mouse has been clicked. What did it click on?
			for (Entity e : entityList.getList()) {
				// Pawn
				if (e.getClass().getName().equals(Pawn.class.getName())) {
					Pawn p = (Pawn)e;
					// Check to see if you clicked one of your pawns
					if (p.getTeam() == PLAYER_TEAM && p.isAt(mouseX, mouseY)) {
						dPrint ("Found pawn");
						// Selected pawn is being swapped for the one that was just clicked on.
						// Select the newly selected pawn.
						selectPawn(p);
						selectedPawn.setPotentialDestination(mouseX, mouseY);
						swappedPawns = true;
						break;
					}
				}
			}
			if (!swappedPawns) {
				// Selecting the destination, because the location clicked was not a pawn.
				selectedPawn.setDestination(selectedPawn.getPotentialDestX(), selectedPawn.getPotentialDestY());
				currentState = TARGET_STATE;
			}
		}
	}
	
	public void updateTargetState(int delta) {
		if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
			// Right click backs out, reverts to the previous state.
			currentState = DESTINATION_STATE;
			selectedPawn.setDestination(-1, -1);
			selectedPawn.setTargetPawn(null);
		}
		else if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			// Mouse has been clicked. What did it click on?
			for (Entity e : entityList.getList()) {
				// Pawn
				if (e.getClass().getName().equals(Pawn.class.getName())) {
					Pawn p = (Pawn)e;
					// Check to see if an opponent was clicked
					if (p.getTeam() != PLAYER_TEAM && p.isAt(mouseX, mouseY)) {
						selectedPawn.setTargetPawn(p);
						currentState = REVIEW_STATE;
						break;
					}
				}
			}
		}
	}
	
	public void updateReviewState(int delta) {
		// Cycle through all your pawns to see if any still need instructions
		readyToGo = true;
		for (Entity e : entityList.getList()) {
			// Pawn
			if (e.getClass().getName().equals(Pawn.class.getName())) {
				Pawn p = (Pawn)e;
				if (!p.hasOrders() && p.getTeam() == PLAYER_TEAM) {
					readyToGo = false;
					break; // out of loop, but not out of state
				}
			}
		}
		
		// A very quick state (most often).
		// When pawns still need orders, pretty much skips straight to the
		// SELECTION_STATE to assign instructions for the next pawn.
		if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON) && selectedPawn != null) {
			// Right click backs out, reverts to the previous state.
			currentState = TARGET_STATE;
			selectedPawn.setTargetPawn(null);
		}
		else if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			deselectPawn();
			// Mouse has been clicked. What did it click on?
			for (Entity e : entityList.getList()) {
				// Pawn
				if (e.getClass().getName().equals(Pawn.class.getName())) {
					Pawn p = (Pawn)e;
					// Check to see if you clicked one of your pawns
					if (p.getTeam() == PLAYER_TEAM && p.isAt(mouseX, mouseY)) {
						selectPawn(p);
						selectedPawn.setPotentialDestination(mouseX, mouseY);
						readyToGo = false;
						
						if (!selectedPawn.hasTarget() && !selectedPawn.hasDestination()) {
							currentState = DESTINATION_STATE;
						}
						break; // out of loop
					}
				}
			}
		}
		
		if (readyToGo) {
			dPrint ("All ready to go (Press Enter to Go)");
			if (input.isKeyPressed(Input.KEY_ENTER)) {
				currentState = GO_STATE;
				for (Entity e : entityList.getList()) {
					// Pawn
					if (e.getClass().getName().equals(Pawn.class.getName())) {
						Pawn p = (Pawn)e;
						p.setFollowingOrders(true);
						// Remove focus from all pawns to stop drawing the 'potential destination'
						p.setHasFocus(false);
						// Calculate angle between starting x,y and destination x,y (shouldn't change this step)
						p.setAngleRadian((float) Math.atan2((p.getDestY() - p.getStartingY()) , (p.getDestX() - p.getStartingX())));
						p.setAngleDegrees(90f + (float) Math.toDegrees(Math.atan2(p.getDestY() - p.getStartingY(), p.getDestX() - p.getStartingX())));
						p.setRotation(p.getAngleDegrees());
					}
				}
			}
		}
	}
	
	public void updateGoState(int delta) {
		// This is where we loop to update the pawns positions.
		// Let's control the update speed so it's uniform across all computers.
		
		boolean hasTime = true;
		
		// This averages the update to a set interval (controlDelta). For example,
		// a controlDelta of 1000 would limit the update to once per second.
		myDelta += delta;
		if (myDelta < controlDelta) {
			return;
		} else {
			turnTime += myDelta;
			myDelta %= controlDelta;
		}
		
		// Check if the turn has run out of time
		if (turnTime >= turnLimit) {
			// force the turnTime so there's no "overflow" when calculating the last update
			turnTime = turnLimit;
			// mark this as the last iteration before moving back to review state
//			currentState = REVIEW_STATE;
			currentState = PICK_ENEMY_MOVES;
			hasTime = false;
		}
		int totalTime = totalGoTime + turnTime;
		formatTime(totalTime);
		
		for (Entity e : entityList.getList()) {
			// Pawn
			if (e.getClass().getName().equals(Pawn.class.getName())) {
				Pawn p = (Pawn)e;
				// Update position based on how much time has passed
				p.update(delta, turnTime);
				
				if (!hasTime) {
					p.setLocation(p.getDestX(), p.getDestY());
					// We reset the pawn back to hasOrders:false
					p.setDestination(-1, -1);
					p.setTargetPawn(null);
					p.setStartingLocation(p.getX(), p.getY());
				}
			}
			// Pawn
			else if (e.getClass().getName().equals(Missile.class.getName())) {
				Missile m = (Missile)e;
				// Update position based on how much time has passed
				try {
					m.update(delta);
				} catch (SlickException e1) {
					e1.printStackTrace();
				}
			}
		}
		if (!hasTime) {
			totalGoTime = totalTime; // totalTime from this last completed round
			turnTime = 0;
			
			// Turn is over, reset the states of each of the pawns.
			for (Entity e : entityList.getList()) {
				// Pawn
				if (e.getClass().getName().equals(Pawn.class.getName())) {
					Pawn p = (Pawn)e;
					p.reset();
				}
			}
		}
	}
	
	/**
	 * Cycle through the enemy pawns, picking their moves. When done,
	 * switch the state so the player can begin assigning their pawns moves.
	 */
	public void pickEnemyMoves() {
		for (Entity e : entityList.getList()) {
			// Pawn
			if (e.getClass().getName().equals(Pawn.class.getName())) {
				Pawn p = (Pawn)e;
				if (p.getTeam() != PLAYER_TEAM) {
					p.pickMove();
				}
			}
		}
		currentState = REVIEW_STATE;
	}
	
	public void formatTime(int time) {
		NumberFormat f = new DecimalFormat("00");
		totalGoString = f.format(time / 1000 / 3600) + ":" + // hours
			f.format(((time / 1000) % 3600) / 60) + ":" + // minutes
			f.format((time / 1000) % 60) + ":" + // seconds
			f.format((time / 10) % 100); // milliseconds (rounded to hundredth)
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new Test());
		//app.setDisplayMode(800, 600, false);
		app.setDisplayMode(854, 480, false);
		app.start();
		
//		float x1 = 10, y1 = 10;
//		int distance = 10;
//		float angle = 45;
//		float x2 = (float) (x1 + distance * Math.cos(Math.toRadians(angle)));
//		float y2 = (float) (y1 + distance * Math.sin(Math.toRadians(angle)));
//		System.out.println ("Move " + distance + " distance from " + x1 + "," + y1 + " to " + x2 + "," + y2 + " at angle Rad(" + Math.toRadians(angle) + ") degree(" + angle + ")");
	}
}
