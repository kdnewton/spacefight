package com.n3wt0n.spacefight.Util.Lighting;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.tiled.TiledMap;

import com.n3wt0n.spacefight.Map.Wall;

/*
 * Based on Kevin Glass's code from http://slick.javaunlimited.net/viewtopic.php?p=16285#16285
 */

public class LightMap {
	
	private String wallLayer = "WALLS";

	private boolean colouredLight = false;
	
	private TiledMap tiledMap;
	
	private float[][][] lightValue;
	private Light mainLight; // Our "player" light-source
	
	private List<Light> lights = new LinkedList<Light>();

	private List<Wall> walls = new LinkedList<Wall>();
	private List<Line> edges = new LinkedList<Line>();
	private List<Polygon> shadows = new LinkedList<Polygon>();

	public LightMap() {
	}
	
	public List<Light> getLights() {
		return lights;
	}
	
	public String getWallLayer() {
		return wallLayer;
	}

	public void setWallLayer(String wallLayer) {
		this.wallLayer = wallLayer;
	}

	public void setLights(List<Light> lights) {
		this.lights = lights;
	}
	
	public void addLight(Light light) {
		this.lights.add(light);
	}

	public void setColouredLight(boolean colouredLight) {
		this.colouredLight = colouredLight;
	}

	public boolean isColouredLight() {
		return colouredLight;
	}

	public void init() {
		lightValue = new float[tiledMap.getWidth()+1][tiledMap.getHeight()+1][3];
		lights.add(mainLight);
		
		updateLightMap();
		walls = prepareWalls(tiledMap);
		edges = prepareEdges(walls);
		shadows = prepareShadows(mainLight.getX(), mainLight.getY(), edges);
	}
	
	public List<Wall> prepareWalls(TiledMap map) {
		List<Wall> list = new LinkedList<Wall>();
		int width = map.getTileWidth();
		int height = map.getTileHeight();
		
		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				if (null != map.getTileImage(x, y, map.getLayerIndex(wallLayer))) {
					Wall w = new Wall(x*width, y*height+1, width, height);
					w.checkNeighbors(tiledMap, wallLayer);
					list.add(w); // y appears to be off by 1 here, so add 1
				}
			}
		}
		
		return list;
	}
	
	public Light getMainLight() {
		return mainLight;
	}

	public void setMainLight(Light mainLight) {
		this.mainLight = mainLight;
	}

	public List<Line> prepareEdges(List<Wall> walls) {
		List<Line> list = new LinkedList<Line>();
		for (Wall w : walls) {
			list.addAll(getVisibleEdges(mainLight.getX(), mainLight.getY(), w));
		}
		return list;
	}
	
	public List<Polygon> prepareShadows(float x, float y, List<Line> edges) {
		List<Polygon> list = new LinkedList<Polygon>();
		
		float shadowLength = 64;
		for (Line e : edges) {
			Polygon shadow = new Polygon();
			
			float[] leftPoint = e.getPoint(0);
			float diffX = x - leftPoint[0];
			float diffY = y - leftPoint[1];
			float angleRadian = (float) Math.atan2(-diffY, -diffX);
			float x2 = (float) (leftPoint[0] + shadowLength * Math.cos(angleRadian));
			float y2 = (float) (leftPoint[1] + shadowLength * Math.sin(angleRadian));

			shadow.addPoint(leftPoint[0], leftPoint[1]);
			shadow.addPoint(x2, y2);
			
			float[] rightPoint = e.getPoint(1);
			diffX = x - rightPoint[0];
			diffY = y - rightPoint[1];
			angleRadian = (float) Math.atan2(-diffY, -diffX);
			x2 = (float) (rightPoint[0] + shadowLength * Math.cos(angleRadian));
			y2 = (float) (rightPoint[1] + shadowLength * Math.sin(angleRadian));
			
			shadow.addPoint(x2, y2);
			shadow.addPoint(rightPoint[0], rightPoint[1]);
			
			list.add(shadow);
		}
		
		return list;
	}
	
	public List<Line> getVisibleEdges(float x, float y, Wall w) {
		List<Line> list = new LinkedList<Line>();
		
		// If somehow the point in question is inside the rectangle, return the empty list
		if (w.contains(x, y)) {
			return list;
		}
		
		float[] topLeft = w.getPoint(0);
		float[] topRight = w.getPoint(1);
		float[] bottomRight = w.getPoint(2);
		float[] bottomLeft = w.getPoint(3);
		
		// When creating the edge, add the points in clock-wise order.
		if (x > topLeft[0] && w.isVisible(w.LEFT)) {
			// Add the left edge: bottom left to top left
			list.add(new Line(bottomLeft[0], bottomLeft[1]+1, topLeft[0], topLeft[1]-1));
		}
		if (y > topRight[1] && w.isVisible(w.TOP)) {
			// Add the top edge: top left to top right
			list.add(new Line(topLeft[0]-1, topLeft[1], topRight[0]+1, topRight[1]));
		}
		if (x < topRight[0] && w.isVisible(w.RIGHT)) {
			// Add the right edge: top right to bottom right
			list.add(new Line(topRight[0], topRight[1]-1, bottomRight[0], bottomRight[1]+1));
		}
		if (y < bottomRight[1] && w.isVisible(w.BOTTOM)) {
			// Add the bottom edge: bottom right to bottom left
			list.add(new Line(bottomRight[0]+1, bottomRight[1], bottomLeft[0]-1, bottomLeft[1]));
		}
		
		return list;
	}
	
	public void update(GameContainer gc, int delta) throws SlickException {
		updateLightMap();
		walls = prepareWalls(tiledMap);
		edges = prepareEdges(walls);
		shadows = prepareShadows(mainLight.getX(), mainLight.getY(), edges);
	}

	public void render(GameContainer gc, Graphics g) throws SlickException {
		// render floor
		tiledMap.render(0, 0, tiledMap.getLayerIndex("FLOOR"));
		// render walls
		tiledMap.render(0, 0, tiledMap.getLayerIndex("WALL"));
		
		// render lightmap
		for (int y = 0; y < tiledMap.getHeight(); y++) {
			for (int x = 0; x < tiledMap.getWidth(); x++) {
				
				Image image = tiledMap.getTileImage(x, y,	tiledMap.getLayerIndex("WALL"));
				if (image == null) {
					image = tiledMap.getTileImage(x, y,	tiledMap.getLayerIndex("FLOOR"));
				}
				
				image.setColor(Image.TOP_LEFT, lightValue[x][y][0], lightValue[x][y][1], lightValue[x][y][2], 1);
				image.setColor(Image.TOP_RIGHT, lightValue[x+1][y][0], lightValue[x+1][y][1], lightValue[x+1][y][2], 1);
				image.setColor(Image.BOTTOM_RIGHT, lightValue[x+1][y+1][0], lightValue[x+1][y+1][1], lightValue[x+1][y+1][2], 1);
				image.setColor(Image.BOTTOM_LEFT, lightValue[x][y+1][0], lightValue[x][y+1][1], lightValue[x][y+1][2], 1);
				image.draw(x*tiledMap.getTileWidth(), y*tiledMap.getTileHeight());
			}
		}
		
		// render shadows
		g.setColor(Color.black);
		for (Polygon s : shadows) {
			g.fill(s);
		}
		
		g.setColor(Color.yellow);
		g.fill(new Circle(mainLight.getX(), mainLight.getY(), 5));
		
//		g.setColor(Color.green);
//		g.draw(new Circle(mainLight.getX(), mainLight.getY(), mainLight.getRadius()));
		
		g.setColor(Color.orange);
//		for (Line e : edges) {
//			g.draw(e);
//		}
		
//		for (Rectangle r : walls) {
//			g.draw(r);
//		}
	}
	
	public TiledMap getTiledMap() {
		return tiledMap;
	}

	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}

	private void updateLightMap() {
		// for every vertex on the map (notice the +1 again accounting for the trailing vertex)
		for (int y=0;y<tiledMap.getHeight()+1;y++) {
			for (int x=0;x<tiledMap.getWidth()+1;x++) {
				// first reset the lighting value for each component (red, green, blue)
				for (int component=0;component<3;component++) {
					lightValue[x][y][component] = 0;
				}
				
				// next cycle through all the lights. Ask each light how much effect
				// it'll have on the current vertex. Combine this value with the currently
				// existing value for the vertex. This lets us blend coloured lighting and 
				// brightness
				for (Light light : lights) {
					float[] effect = getEffectAt(light, x, y);
					for (int component=0;component<3;component++) {
						lightValue[x][y][component] += effect[component];
					}
				}
				
				// finally clamp the components to 1, since we don't want to 
				// blow up over the colour values
				for (int component=0;component<3;component++) {
					if (lightValue[x][y][component] > 1) {
						lightValue[x][y][component] = 1;
					}
				}
			}
		}
	}
	
	public float[] getEffectAt(Light light, float mapX, float mapY) {
		float xpos = light.getX() / tiledMap.getTileWidth();
		float ypos = light.getY() / tiledMap.getTileHeight();
		float dx = (mapX - xpos);
		float dy = (mapY - ypos);
		float distance2 = (dx*dx) + (dy*dy);
		float strength = light.getRadius()/tiledMap.getTileWidth();
		float effect = 1 - (distance2 / (strength * strength));
		
		if (effect < 0) effect = 0;
		
		if (colouredLight) {
			return new float[] {light.getColor().r * effect, light.getColor().g * effect, light.getColor().b * effect};
		} else {
			return new float[] {effect, effect, effect};
		}
	}

//	public static void main(String[] args) throws SlickException {
//		AppGameContainer app = new AppGameContainer(new LightMap());
//		app.setDisplayMode(512, 512, false);
//		app.start();
//	}
}