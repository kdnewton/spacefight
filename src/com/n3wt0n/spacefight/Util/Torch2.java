package com.n3wt0n.spacefight.Util;

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
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

public class Torch2 extends BasicGame {
	
	private TiledMap tiledMap;
	
	private float x, y;
	private float speed, radius;
	private float curDeg = 0, incrementDeg = 1.25f;
	private float curLength = 0, checkLength = 3.1f; // distance to increase ray when casting
	
	private List<Rectangle> walls;
	private List<Line> lines;
	Circle player;

	public Torch2() throws SlickException {
		super("Torch2 Test");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		// This map has 2 layers.
		// "FLOOR" which renders first. Player can walk over this.
		// "WALL" which renders second. Gets in the players way, has to walk around.
		tiledMap = new TiledMap("media/map/torch.tmx");
		
		x = 256; // Absolute position, in pixels.
		y = 256;
		speed = 2f;
		radius = 200f; // Distance in pixels.
		
		player = new Circle(x, y, 5);
		setWalls(prepWalls(tiledMap));
		
		lines = getPath(player);
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
	
	public List<Line> getPath(Circle p) {
		List<Line> list = new LinkedList<Line>();

		curDeg = 0;
		while (curDeg <= 360) {
//			list.add(getLine(curDeg, p.getCenterX(), p.getCenterY(), checkLength));
			list.add(getLine(curDeg, p.getCenterX(), p.getCenterY(), 0, checkLength));
			curDeg += incrementDeg;
		}
		
		return list;
	}
	
	public Line getLine(float deg, float x, float y, float increment) {
		float newX, newY, prevX, prevY;
		prevX = newX = x;
		prevY = newY = y;
		
		curLength = 0;
		while (curLength <= radius) {
			newX = (float) (this.x + curLength * Math.cos(Math.toRadians(deg)));
			newY = (float) (this.y + curLength * Math.sin(Math.toRadians(deg)));
			Image image = tiledMap.getTileImage((int)(newX/tiledMap.getTileWidth()), (int)(newY/tiledMap.getTileHeight()), tiledMap.getLayerIndex("WALL"));
			if (image != null) {
				newX = prevX;
				newY = prevY;
				// Increment precisely to get point where collision occurs
				if (increment > 1) {
					Line n = getLine(deg, newX, newY, 1);
					newX = n.getX2();
					newY = n.getY2();
				}
				break;
			}
			curLength += increment;
			prevX = newX;
			prevY = newY;
		}
		return new Line(x, y, newX, newY);
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
		
		newX = (float) (this.x + curLength * Math.cos(Math.toRadians(deg)));
		newY = (float) (this.y + curLength * Math.sin(Math.toRadians(deg)));
		
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
		lines = getPath(player);
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		// Render map layers
		for (int i = 0; i < tiledMap.getLayerCount(); i++) {
			tiledMap.render(0, 0, i);
		}
		
		// Render the path
		g.setColor(Color.orange);
		for (Line l : lines) {
			g.drawLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
		}
		
		// Render "player"
		g.setColor(Color.yellow);
		g.fill(player);
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new Torch2());
		app.setDisplayMode(512, 512, false);
		app.start();
	}

	public void setWalls(List<Rectangle> walls) {
		this.walls = walls;
	}

	public List<Rectangle> getWalls() {
		return walls;
	}
}