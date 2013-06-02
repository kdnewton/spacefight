package com.n3wt0n.spacefight.Util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

import com.n3wt0n.spacefight.Map.Wall;
import com.n3wt0n.spacefight.Util.Lighting.Light;
import com.n3wt0n.spacefight.Util.Lighting.LightMap;

public class Torch3 extends BasicGame {
	
	private TiledMap tiledMap;
	private LightMap lightMap;
	
	private float speed;
	
	private List<Wall> walls = new LinkedList<Wall>();
	private Light player;
	
	private List<Light> visibleNodes = new LinkedList<Light>();

	public Torch3() throws SlickException {
		super("Torch 3 Test");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		tiledMap = new TiledMap("media/map/torch.tmx");
		player = new Light(224, 224, 175, Color.red);
		
		lightMap = new LightMap();
		lightMap.setWallLayer("WALL");
		lightMap.setTiledMap(tiledMap);
		lightMap.setColouredLight(false);
		
		lightMap.setMainLight(player);
		lightMap.addLight(new Light(64, 64, 200));
		
		lightMap.init();
		
		speed = 2f;
		
		walls = lightMap.prepareWalls(tiledMap);
		
		visibleNodes = findValidPoints(player.getX(), player.getY(), player.getRadius(), walls);
	}
	
	public List<Rectangle> prepWalls(TiledMap map) {
		List<Rectangle> list = new LinkedList<Rectangle>();
		int width = map.getTileWidth();
		int height = map.getTileHeight();
		
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				if (null != map.getTileImage(x, y, map.getLayerIndex("WALL"))) {
					list.add(new Rectangle(x*width+1, y*height+1, width, height));
				}
			}
		}
		
		return list;
	}
	
	public List<Light> findValidPoints(float x, float y, float radius, List<Wall> walls) {
		LinkedList<Light> points = new LinkedList<Light>();
		Circle range = new Circle(x, y, radius);
		
		float pointRadius = 3;
		float locationAdjustment = 1.5f;
		
		// Iterate the list of walls and add any points found
		// within range of (x, y) to the list of valid points
		for (Rectangle r : walls) {
			// Find mean location of 4 points to compare positions later and adjust location as necessary
			float meanX = -1, meanY = -1;
			for (int i = 0; i < r.getPointCount(); i++) {
				meanX += r.getPoint(i)[0];
				meanY += r.getPoint(i)[1];
			}
			meanX /= r.getPointCount();
			meanY /= r.getPointCount();
			
			for (int i = 0; i < r.getPointCount(); i++) {
				float rX = r.getPoint(i)[0];
				float rY = r.getPoint(i)[1];
				if (rX == x && rY == y) {
					// It's the same point! Skip it.
					continue;
				}
				// Adjust points as necessary using the mean location
				rX = (rX < meanX) ? (rX-locationAdjustment) : (rX+locationAdjustment);
				rY = (rY < meanY) ? (rY-locationAdjustment) : (rY+locationAdjustment);
				if (range.contains(rX, rY) && inLineOfSight(x, y, rX, rY)) {
					Light p = new Light(rX, rY, pointRadius);
					points.add(p);
				}
			}
		}
		
		// Sort points from closest to furthest distance from (x, y)
		DistanceCompare comp = new DistanceCompare();
		comp.setPlayerX(x);
		comp.setPlayerY(y);
		Collections.sort(points, comp);
		
		return points;
	}
	
	public boolean inLineOfSight(float x1, float y1, float x2, float y2) {
		float increment = 1.0f;
		float curLength = 0;
		
		// Get angle between two points
		float diffX = x2 - x1;
		float diffY = y2 - y1;
		float distance = (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2)) - (tiledMap.getTileWidth()/2);
		float angleRadian = (float) Math.atan2(diffY, diffX);
		
		float newX = x1;
		float newY = y1;
		
		while (curLength < distance) {
			
			curLength += increment;
			if (curLength >= distance) {
				return true;
			}
			
			// scan up the line until either the point is reached or a collision is detected
			newX = (float) (x1 + curLength * Math.cos(angleRadian));
			newY = (float) (y1 + curLength * Math.sin(angleRadian));
			
			Image image = tiledMap.getTileImage((int)(newX/tiledMap.getTileWidth()), (int)(newY/tiledMap.getTileHeight()), tiledMap.getLayerIndex("WALL"));
			if (image != null) {
				// Collision detected
				return false;
			}
		};
		
		// Should never return here, but 'true' anyway
		return true;
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		Input input = gc.getInput();
		// Keyboard Inputs
		if (input.isKeyDown(Input.KEY_LEFT))	player.setX(player.getX() - speed);
		if (input.isKeyDown(Input.KEY_UP))		player.setY(player.getY() - speed);
		if (input.isKeyDown(Input.KEY_RIGHT))	player.setX(player.getX() + speed);
		if (input.isKeyDown(Input.KEY_DOWN))	player.setY(player.getY() + speed);

		visibleNodes = findValidPoints(player.getX(), player.getY(), player.getRadius(), walls);
		lightMap.update(gc, delta);
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		// Render map layers
//		for (int i = 0; i < tiledMap.getLayerCount(); i++) {
//			tiledMap.render(0, 0, i);
//		}
		
		lightMap.render(gc, g);
		
		// Render "player"
		g.setColor(Color.yellow);
		g.fill(new Circle(player.getX(), player.getY(), 5));
		
		g.setColor(Color.green);
		g.draw(new Circle(player.getX(), player.getY(), player.getRadius()));
		
		g.setColor(Color.red);
		for (Light c : visibleNodes) {
			g.fill(new Circle(c.getX(), c.getY(), c.getRadius()));
		}
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new Torch3());
		app.setTargetFrameRate(60);
		app.setDisplayMode(512, 512, false);
		app.start();
	}
	
	/**
	 * Compares the distances between two points and their relation to the player
	 * @author Kyle Newton
	 *
	 */
	private class DistanceCompare implements Comparator<Light> {
		
		private float playerX, playerY;
		
		public void setPlayerX(float x) {
			playerX = x;
		}
		
		public void setPlayerY(float y) {
			playerY = y;
		}
		
		@Override
		public int compare(Light a, Light b) {
			float diffX = playerX - a.getX();
			float diffY = playerY - a.getY();
			float aDist = (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
		
			diffX = playerX - b.getX();
			diffY = playerY - b.getY();
			float bDist = (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
			
			if (aDist == bDist) return 0;
			return (aDist < bDist) ? -1 : 1;
		}
	}
}