package com.n3wt0n.spacefight.Entities;

import com.n3wt0n.spacefight.Util.EntityList;

public class Intelligence {
	
	private EntityList pawnlist;
	private Pawn self;
	private Pawn target;

	private static final int TARGET_CLOSEST = 1;
	private static final int TARGET_FURTHEST = 2;
	private static final int TARGET_RANDOM = 3;

	private static final int MOVE_TOWARD = 1;
	private static final int MOVE_PERPENDICULAR = 2;
	
	private int targetType = TARGET_CLOSEST;
	private int moveType = MOVE_TOWARD;
	
	public Intelligence(Pawn self, EntityList pawnlist) {
		this.self = self;
		this.pawnlist = pawnlist;
	}
	
	public void go() {
		switch (targetType) {
		case TARGET_CLOSEST:
			target = findClosestPawn();
			break;
		default:
			target = findClosestPawn();
			break;
		}
		self.setTargetPawn(target);
		
		switch (moveType) {
		case MOVE_TOWARD:
			moveTowardTarget();
			break;
		default:
			moveTowardTarget();
			break;
		}
	}
	
	/**
	 * Sorts through list of pawns and returns the closest pawn to self
	 * @return Pawn
	 */
	public Pawn findClosestPawn() {
		float dist = Integer.MAX_VALUE;
		Pawn closest = null;
		for (Entity e : pawnlist.getList()) {
			if (e.getClass().getName().equals(Pawn.class.getName())) {
				Pawn p = (Pawn)e;
				float diffX;
				float diffY;
				float newDist;
				if (p.getTeam() == self.getTeam()) {
					// Skip over teammates
					continue;
				}
				diffX = self.getX() - e.getX();
				diffY = self.getY() - e.getY();
				newDist = (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
				if (newDist < dist) {
					closest = p;
					dist = newDist;
				}
			}
		}
		return closest;
	}
	
	/**
	 * Move toward the target
	 */
	public void moveTowardTarget() {
		self.setPotentialDestination(target.getX(), target.getY());
		self.limitRange();
		self.setDestination(self.getPotentialDestX(), self.getPotentialDestY());
	}
}
