package rushAdvantage;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						// Spawn a soldier
						Direction desiredDir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						Direction dir = getSpawnDirection(rc, desiredDir);
						if (dir != null) {
							rc.spawn(dir);
						}
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						Direction desiredDir;
						if (checkFriendlyRobotsNearby(rc, 32, 4) ) { // if there are 4 buddies nearby
							if (checkEnemyRobotsNearby(rc, 14, 1) == false) { // if there aren't enemies nearby at all
								desiredDir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
							} else {
								Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, 14, rc.getTeam().opponent());
								int closestDist = 1000000;
								MapLocation closestEnemy=null;
								for (int i=0;i<enemyRobots.length;i++){
									Robot arobot = enemyRobots[i];
									RobotInfo arobotInfo = rc.senseRobotInfo(arobot);
									int dist = arobotInfo.location.distanceSquaredTo(rc.getLocation());
									if (dist<closestDist){
										closestDist = dist;
										closestEnemy = arobotInfo.location;
									}
								}
								
								desiredDir = rc.getLocation().directionTo(closestEnemy); 
							}
							
						} 
						else { // if there aren't enough buddies
							desiredDir = rc.getLocation().directionTo(rc.senseHQLocation());
						}
						Direction dir = getMovementDirection(rc, desiredDir);
						if (badBomb(rc, rc.getLocation().add(dir))) {
							rc.defuseMine(rc.getLocation().add(dir));
						} else {
							rc.move(dir);
							rc.setIndicatorString(0, "Last direction moved: "+dir.toString());
						}
						
					}
				}

				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * helper fcn to see if there are >= n enemy robots within range of given parameter range
	 * @param rc
	 * @param range
	 * @param n
	 * @return
	 */
	private static boolean checkEnemyRobotsNearby (RobotController rc, int range, int n) {
		if (rc.senseNearbyGameObjects(Robot.class, range, rc.getTeam().opponent()).length >= n) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * helper fcn to see what direction to actually go given a desired direction
	 * @param rc
	 * @param dir
	 * @return
	 */
	private static Direction getMovementDirection(RobotController rc, Direction dir) {
		if (rc.canMove(dir)) {
			return dir;
		} else if (rc.canMove(dir.rotateLeft())) {
			return dir.rotateLeft();
		} else if (rc.canMove(dir.rotateRight())) {
			return dir.rotateRight();
		} else {
			return null;
		}
	}
	
	/**
	 * helper fcn to see what direction to actually go given a desired direction
	 * @param rc
	 * @param dir
	 * @return
	 */
	private static Direction getSpawnDirection(RobotController rc, Direction dir) {
		if (rc.canMove(dir)) {
			return dir;
		} else if (rc.canMove(dir.rotateLeft())) {
			return dir.rotateLeft();
		} else if (rc.canMove(dir.rotateRight())) {
			return dir.rotateRight();
		} else if (rc.canMove(dir.rotateLeft().rotateLeft())) {
			return dir.rotateLeft().rotateLeft();
		} else if (rc.canMove(dir.rotateRight().rotateRight())) {
			return dir.rotateRight().rotateRight();
		} else if (rc.canMove(dir.rotateLeft().opposite())) {
			return dir.rotateLeft().opposite();
		} else if (rc.canMove(dir.rotateRight().opposite())) {
			return dir.rotateRight().opposite();
		} else {
			return dir.opposite();
		}
	}
	
	/**
	 * helper fcn to compute if location contains badbomb
	 * @param rc
	 * @param loc
	 * @return
	 */
	private static boolean badBomb(RobotController rc, MapLocation loc) {
		Team isBomb = rc.senseMine(loc);
		if (isBomb == null || isBomb == rc.getTeam()) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Helper fcn to see if there are >= n friendly robots within a range of the robot
	 * @param rc
	 * @param range
	 * @param n
	 * @return
	 */
	private static boolean checkFriendlyRobotsNearby (RobotController rc, int range, int n) {
		if (rc.senseNearbyGameObjects(Robot.class, range, rc.getTeam()).length >= n) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean checkNumericalAdvantage (RobotController rc, int range) {
		if (rc.senseNearbyGameObjects(Robot.class, range, rc.getTeam()).length >= 
				rc.senseNearbyGameObjects(Robot.class, range, rc.getTeam().opponent()).length) {
			return true;
		} else {
			return false;
		}
	}
}
