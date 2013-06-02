package com.n3wt0n.spacefight.Util;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.tiled.TiledMap;

public class AreaMapExample extends BasicGame {
	
	private TiledMap tiledMap;
	private AccessibleAreaMap areaMap;
	private Circle player;
	private float speed;

	public AreaMapExample() throws SlickException {
		super("Area Map Test");
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		speed = 2;
		
		tiledMap = new TiledMap("media/map/torch.tmx");
		player = new Circle(200, 100, 100);
		
		areaMap = new AccessibleAreaMap(tiledMap, player.getCenterX(), player.getCenterY(), player.getRadius());
		areaMap.setWallLayer("WALL");
		getAreaMap();
	}
	
	public void getAreaMap() {
		areaMap.init();
	}
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		Input input = gc.getInput();
		// Keyboard Inputs
		if (input.isKeyDown(Input.KEY_LEFT))	player.setCenterX(player.getCenterX() - speed);
		if (input.isKeyDown(Input.KEY_UP))		player.setCenterY(player.getCenterY() - speed);
		if (input.isKeyDown(Input.KEY_RIGHT))	player.setCenterX(player.getCenterX() + speed);
		if (input.isKeyDown(Input.KEY_DOWN))	player.setCenterY(player.getCenterY() + speed);
		
		if (input.isKeyPressed(Input.KEY_M)) {
			areaMap.setX(player.getCenterX());
			areaMap.setY(player.getCenterY());
			getAreaMap();
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		// Render map layers
		for (int i = 0; i < tiledMap.getLayerCount(); i++) {
			tiledMap.render(0, 0, i);
		}
		
		areaMap.render(g);
		
		g.setColor(Color.red);
		for (Circle c : areaMap.getValidPoints()) {
			g.fill(c);
		}
		
		g.setColor(Color.yellow);
		g.fill(new Circle(player.getCenterX(), player.getCenterY(), 5));
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new AreaMapExample());
		app.setTargetFrameRate(60);
		app.setDisplayMode(512, 512, false);
		app.start();
	}
}