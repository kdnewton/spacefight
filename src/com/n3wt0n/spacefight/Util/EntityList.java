package com.n3wt0n.spacefight.Util;

import java.util.LinkedList;
import java.util.List;

import com.n3wt0n.spacefight.Entities.Entity;

public class EntityList {
	
	private List<Entity> list;
	private List<Entity> buffer; // Usually bullets are put here, then at the end of the update they are added to the main list
	private List<Entity> graveyard;
	private Entity lastEntity;
	
	public EntityList() {
		list = new LinkedList<Entity>();
		buffer = new LinkedList<Entity>();
		graveyard = new LinkedList<Entity>();
	}
	
	public List<Entity> getList() {
		return list;
	}
	
	public List<Entity> getBuffer() {
		return buffer;
	}
	
	public List<Entity> getGraveyard() {
		return graveyard;
	}
	
	public void add(Entity entity) {
		list.add(entity);
		lastEntity = entity;
	}
	
	public void addToBuffer(Entity entity) {
		buffer.add(entity);
	}
	
	public void addToGraveyard(Entity entity) {
		graveyard.add(entity);
	}
	
	public Entity getLast() {
		return lastEntity;
	}
	
	public void bufferToList() {
		list.addAll(getBuffer());
		buffer.clear();
	}
	
	public void clearGraveyard() {
		list.removeAll(graveyard);
		graveyard.clear();
	}
	
	public void cleanup() {
		// Move the new Pawns and Missiles in the buffer list over to the main entity list
		bufferToList();
		// and clean up the graveyard
		clearGraveyard();
	}
}
