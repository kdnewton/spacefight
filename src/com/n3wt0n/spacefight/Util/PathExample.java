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
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.tiled.TiledMap;

public class PathExample extends BasicGame {
	
	private TiledMap tiledMap;
	
	private float x, y;
	private float speed, radius;
	private float curDeg = 0, incrementDeg = 1.8f;
	private float incrementLength = 3.1f; // distance to increase ray when casting
	
	private List<Rectangle> walls = new LinkedList<Rectangle>();
	private List<Polygon> area = new LinkedList<Polygon>();
	private Circle player;

	public PathExample() throws SlickException {
		super("Path Test");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		// This map has 2 layers.
		// "FLOOR" which renders first. Player can walk over this.
		// "WALL" which renders second. Gets in the players way, has to walk around.
		tiledMap = new TiledMap("media/map/torch.tmx");
		
		x = 224; // Absolute position, in pixels.
		y = 224;
		speed = 2f;
		radius = 128f; // Distance in pixels.
		
		player = new Circle(x, y, 5);
		walls = prepWalls(tiledMap);
		
		area = mapAccessibleArea(player.getCenterX(), player.getCenterY(), radius, walls);
		// TODO: See about adding all the walls together into shapes of connected walls to reduce the number of points
		// TODO: Apply a texture to the area list to nicely display the accessible area
	}
	
	public Shape joinShapes(Shape a, Shape b) {
		if (!a.intersects(b)) {
			return a;
		}
		Polygon p = new Polygon();
		Shape[] shape = a.union(b);
		for (Shape s : shape) {
			for (int i = 0; i < s.getPointCount(); i++) {
				p.addPoint(s.getPoint(i)[0], s.getPoint(i)[1]);
			}
		}
		return p;
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
	
	public List<Line> getPath(float x1, float y1, float x2, float y2, float incrementLength, float radius) {
		List<Line> list = new LinkedList<Line>();

		curDeg = 0;
		while (curDeg <= 360) {
			list.add(getLine(curDeg, x1, y1, x2, y2, 0, incrementLength, radius));
//			list.add(getLine(curDeg, p.getCenterX(), p.getCenterY(), 0, checkLength));
			curDeg += incrementDeg;
		}
		
		return list;
	}
	
	public List<Polygon> mapAccessibleArea(float x, float y, float r, List<Rectangle> w) {
		float distanceTolerance = 2f;
		
		List<Polygon> area = new LinkedList<Polygon>();
		if (r <= distanceTolerance) {
			return area;
		}
		
		List<Line> lines = getPath(x, y, x, y, incrementLength, r);
		float prevX = 0;
		float prevY = 0;
		float firstX = 0;
		float firstY = 0;
		int i = 0;
		for (Line line : lines) {
			if (i == 0) {
				firstX = prevX = line.getX2();
				firstY = prevY = line.getY2();
				i = 1;
				continue;
			}
			Polygon poly = new Polygon();
			poly.addPoint(prevX, prevY);
			poly.addPoint(line.getX2(), line.getY2());
			poly.addPoint(x, y);
			prevX = line.getX2();
			prevY = line.getY2();
			area.add(poly);
		}
		Polygon poly = new Polygon();
		poly.addPoint(firstX, firstY);
		poly.addPoint(prevX, prevY);
		poly.addPoint(x, y);
		area.add(poly);
		
		List<Circle> validPoints = findValidPoints(x, y, r, w);
		for (Circle c : validPoints) {
			float distFromP = getDist(c.getCenterX(), c.getCenterY(), x, y);
			float newRadius = r - distFromP;
			
			// If the two points are relatively close (side by side)
			if (distFromP <= distanceTolerance) {
				continue;
			}
			
			area.addAll(mapAccessibleArea(c.getCenterX(), c.getCenterY(), newRadius, w));
		}
		return area;
	}
	
	public Line getLine(float deg, float x1, float y1, float x2, float y2, float curLength, float increment, float radius) {
		float newX, newY, prevX, prevY;
		prevX = newX = x2;
		prevY = newY = y2;
		
		while (curLength <= radius) {
			newX = (float) (x1 + curLength * Math.cos(Math.toRadians(deg)));
			newY = (float) (y1 + curLength * Math.sin(Math.toRadians(deg)));
			Image image = tiledMap.getTileImage((int)(newX/tiledMap.getTileWidth()), (int)(newY/tiledMap.getTileHeight()), tiledMap.getLayerIndex("WALL"));
			if (image != null) {
				// Found a wall. Roll back to previous position and proceed forward carefully.
				newX = prevX;
				newY = prevY;
				// Increment precisely to get point where collision occurs
				if (increment > 1) {
					// OK, a little recursion here, too
					curLength -= increment;
					Line n = getLine(deg, x1, y1, newX, newY, curLength, 1, radius);
					newX = n.getX2();
					newY = n.getY2();
				}
				break;
			}
			curLength += increment;
			prevX = newX;
			prevY = newY;
		}
		return new Line(x1, y1, newX, newY);
	}
	
	/**
	 * It's recursive... and it's harder to read than the while-loop that does the same thing.
	 * @param deg Where to point the line
	 * @param x The starting point of the line segment
	 * @param y The starting point of the line segment
	 * @param curLength The current overall length of the line segments
	 * @param increment How much to increment the line segment
	 * @return The line segment from the player position until collision with a wall, or the max radius
	 */
	public Line getLine(float deg, float x, float y, float curLength, float increment) {
		float newX, newY, prevX, prevY;
		prevX = newX = x;
		prevY = newY = y;
		
		curLength += increment;
		if (curLength >= radius) {
			return new Line(x, y, newX, newY);
		}
		
		newX = (float) (x + curLength * Math.cos(Math.toRadians(deg)));
		newY = (float) (y + curLength * Math.sin(Math.toRadians(deg)));
		
		Line n = null;
		Image image = tiledMap.getTileImage((int)(newX/tiledMap.getTileWidth()), (int)(newY/tiledMap.getTileHeight()), tiledMap.getLayerIndex("WALL"));
		if (image == null) {
			// No wall found, keep going
			n = getLine(deg, newX, newY, curLength, increment);
		}
		else {
			// Wall found
			if (increment <= 1) {
				// Found a wall!
				return new Line(x, y, prevX, prevY);
			}
			else {
				// Back up to the previous iteration and creep forward a little at a time
				n = getLine(deg, prevX, prevY, (curLength-increment), 1f);
			}
		}
		newX = n.getX2();
		newY = n.getY2();
		return new Line(x, y, newX, newY);
	}
	
	public List<Circle> findValidPoints(float x, float y, float radius, List<Rectangle> walls) {
		LinkedList<Circle> points = new LinkedList<Circle>();
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
					Circle p = new Circle(rX, rY, pointRadius);
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
	
	public float getDist(float x1, float y1, float x2, float y2) {
		float diffX = x1-x2;
		float diffY = y1-y2;
		return (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
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
		if (input.isKeyDown(Input.KEY_LEFT))	x -= speed;
		if (input.isKeyDown(Input.KEY_UP))		y -= speed;
		if (input.isKeyDown(Input.KEY_RIGHT))	x += speed;
		if (input.isKeyDown(Input.KEY_DOWN))	y += speed;
		player.setCenterX(x);
		player.setCenterY(y);
		
		if (input.isKeyPressed(Input.KEY_M)) {
			area = mapAccessibleArea(player.getCenterX(), player.getCenterY(), radius, walls);
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		// Render map layers
		for (int i = 0; i < tiledMap.getLayerCount(); i++) {
			tiledMap.render(0, 0, i);
		}
		
		// Render the path
		g.setColor(Color.orange);
		for (Polygon p : area) {
			g.draw(p);
		}
		
		// Render "player"
		g.setColor(Color.yellow);
		g.fill(player);
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new PathExample());
		app.setTargetFrameRate(60);
		app.setDisplayMode(512, 512, false);
		app.start();
	}
	
	/**
	 * Compares the distances between two points and their relation to the player
	 * @author Kyle Newton
	 *
	 */
	private class DistanceCompare implements Comparator<Shape> {
		
		private float playerX, playerY;
		
		public void setPlayerX(float x) {
			playerX = x;
		}
		
		public void setPlayerY(float y) {
			playerY = y;
		}
		
		@Override
		public int compare(Shape a, Shape b) {
			float diffX = playerX - a.getCenterX();
			float diffY = playerY - a.getCenterY();
			float aDist = (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
		
			diffX = playerX - b.getCenterX();
			diffY = playerY - b.getCenterY();
			float bDist = (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
			
			if (aDist == bDist) return 0;
			return (aDist < bDist) ? -1 : 1;
		}
	}
}