package com.n3wt0n.spacefight.Map;

import org.newdawn.slick.tiled.TiledMap;

public class Map {

	protected TiledMap tiledMap;

	protected int mapWidth;
	protected int mapHeight;

	/**
	 * Create a MapUtil
	 * 
	 * @param tiledMap The TiledMap to build a physical world of.
	 * @param world The Phys2D World to build.
	 */
	public Map(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
		mapWidth = tiledMap.getWidth()*tiledMap.getTileWidth();
		mapHeight = tiledMap.getHeight()*tiledMap.getTileHeight();
	}
	
	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public TiledMap getTiledMap() {
		return tiledMap;
	}

	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}

	/**
	 * Check to see if a foreground tile is found at location (x,y)
	 * 
	 * @param x The location along the x-plane.
	 * @param y The location along the y-plane.
	 * @return True if there is a foreground tile at that location.
	 */
	public boolean isForegroundAtLocation(int x, int y) {
		x = x / tiledMap.getTileWidth();
		y = y / tiledMap.getTileHeight();
		return (tiledMap.getTileImage(x, y, tiledMap.getLayerIndex("FOREGROUND")) == null) ? false
				: true;
	}

	/**
	 * Check to see if a certain type (FOREGROUND, BACKGROUND, PLATFORMS) tile is found at location (x,y)
	 * 
	 * @param x The location along the x-plane.
	 * @param y The location along the y-plane.
	 * @return True if there is a foreground tile at that location.
	 */
	public boolean isTileTypeAt(int x, int y, String type) {
		boolean result = false;
		try {
			x = x / tiledMap.getTileWidth();
			y = y / tiledMap.getTileHeight();
			result = (tiledMap.getTileImage(x, y, tiledMap.getLayerIndex(type)) == null) ? false : true;
		} catch (Exception e) {

		}
		return result;
	}
	
	/**
	 * Check to see if the tile at location (x,y) is part of the foreground.
	 * 
	 * @param x The location along the x-plane.
	 * @param y The location along the y-plane.
	 * @return True if the tile is part of the foreground.
	 */
	public boolean isForegroundAtTile(int x, int y) {
		return (tiledMap.getTileImage(x, y, tiledMap.getLayerIndex("FOREGROUND")) == null) ? false
				: true;
	}

	/**
	 * Read the TiledMap properties and build the physical world.
	 */
	public void buildMap() {
		System.out.println ("Building Map");
		int tilesWide = tiledMap.getWidth();
		int tilesHigh = tiledMap.getHeight();
//		int tileWidth = tiledMap.getTileWidth();
//		int tileHeight = tiledMap.getTileHeight();
		
		for (int y = 0; y < tilesHigh; y++) {
			for (int x = 0; x < tilesWide; x++) {
				if (tiledMap.getTileImage(x, y, tiledMap.getLayerIndex("MAIN")) != null) {
					while ((x + 1) < tilesWide && tiledMap.getTileImage(x + 1, y, tiledMap.getLayerIndex("MAIN")) != null) {
						x++;
					}
				}
			}
		}
	}
}
