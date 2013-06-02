package com.n3wt0n.spacefight.Util;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import com.n3wt0n.spacefight.Entities.Entity;
import com.n3wt0n.spacefight.Map.Map;

public class Camera {
	
	private Map map;
	
	// The camera's position
	private int x, y;
	private float scrollSpeed;
	
	// The TOP LEFT tile of the map that is drawn
	// When tiles are off screen, they do not need to be drawn.
	private int tileOffsetX = 0, tileOffsetY = 0;
	private int tileIndexX;
	private int tileIndexY;
	
	private int tileWidth, tileHeight;
	private int mapWidth, mapHeight;
	private int screenWidth, screenHeight;
	
	// initial number of pixels to the right of the screen.
	protected int initDiffX = 0;
	protected int initDiffY = 0;
	// Pixels of map still to the right of the screen.
	protected int screenRight = 0;
	protected int screenDown = 0;
	
	private EntityList entityList;
	
	public Camera(GameContainer gc, float scrollSpeed, Map map) {
		this.scrollSpeed = scrollSpeed;
		this.map = map;
		
		this.mapWidth = map.getMapWidth();
		this.mapHeight = map.getMapHeight();
		
		this.tileWidth = map.getTiledMap().getTileWidth();
		this.tileHeight = map.getTiledMap().getTileHeight();
		
		this.screenWidth = gc.getWidth();
		this.screenHeight = gc.getHeight();
		
		init();
	}
	
	public void init() {
		initDiffX = mapWidth - screenWidth;
		initDiffY = mapHeight - screenHeight;
		screenRight = initDiffX;
		screenDown = initDiffY;
	}
	
	public Map getMap() {
		return map;
	}
	
	public void setMap(Map map) {
		this.map = map;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setScrollSpeed(float scrollSpeed) {
		this.scrollSpeed = scrollSpeed;
	}
	
	public void setEntityList(EntityList pawnList) {
		this.entityList = pawnList;
	}
	
	public void update() {
		
		// When scrolling all the way right, fit the map to the right side of the screen
		if ((initDiffX + x) < 0) {
			screenRight = 0;
			x = -1*initDiffX;
		}
		
		// When scrolling all the way left, fit the map to the left side of the screen
		if (x > 0) {
			x = 0;
		}
		
		// When scrolling all the way down, fit the map to the bottom of the screen
		if ((initDiffY + y) < 0) {
			screenDown = 0;
			y = -1*initDiffY;
		}
		
		// When scrolling all the way up, fit the map to the top of the screen
		if (y > 0) {
			y = 0;
		}
	}
	
	public void render(GameContainer gc, Graphics g, String totalTime) throws SlickException {
		g.translate(x, y);
		
//		System.out.println ("Cam: " + x + ", " + y);
//		System.out.println ("Map: " + map.getMapWidth() + ", " + map.getMapHeight());
//		System.out.println ("Screen: " + screenWidth + ", " + screenHeight);
		
		tileOffsetX = (x % tileWidth);
		tileOffsetY = (y % tileHeight);
		tileIndexX = -1 * (x / tileWidth);
		tileIndexY = -1 * (y / tileHeight);
		
		renderLayer(gc, "BACKGROUND");
		renderLayer(gc, "MAIN");
		renderLayer(gc, "WALLS");

		for (Entity e : entityList.getList()) {
			e.render(g);
		}
		
		renderLayer(gc, "FOREGROUND");

		g.translate(-x, -y);
		g.drawString(totalTime, 20, (gc.getHeight()-20));
	}

	/**
	 * Render a TiledMap layer with name layerName.
	 * 
	 * @param layerName
	 *            The name of the layer to render (BACKGROUND, MAIN, FOREGROUND).
	 */
	private void renderLayer(GameContainer gc, String layerName) {
		int posX = -this.x + tileOffsetX;
		int posY = -this.y + tileOffsetY;
		int width = (gc.getWidth() - tileOffsetX) / tileWidth + 1;
		int height = (gc.getHeight() - tileOffsetY) / tileHeight + 1;
		map.getTiledMap().render(
				posX, // Render at posX and posY because we use g.translate(x, y) previously
				posY,
				tileIndexX,
				tileIndexY,
				width,
				height,
				map.getTiledMap().getLayerIndex(layerName), false);
		
//		System.out.println ("Render: " +
//				posX + ", " +
//				posY + ", " +
//				tileIndexX + ", " +
//				tileIndexY + ", " +
//				width + ", " +
//				height + ", "
//			);
	}
	
	public void scrollLeft() {
		if (x < 0) {
			this.x += scrollSpeed;
		}
	}
	
	public void scrollUp() {
		if (y < 0) {
			this.y += scrollSpeed;
		}
	}
	
	public void scrollRight() {
		if (screenRight >= 0) {
			this.x -= scrollSpeed;
		}
	}
	
	public void scrollDown() {
		if (screenDown >= 0) {
			this.y -= scrollSpeed;
		}
	}
}
