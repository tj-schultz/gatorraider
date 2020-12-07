// Tyler J. Schultz
// COP3502
// StudentAttackerController Class

package edu.ufl.cise.cs1.controllers;
import com.sun.javafx.stage.PopupWindowPeerListener;
import game.controllers.AttackerController;
import game.models.*;
import java.awt.*;
import java.util.List;
import java.util.ListResourceBundle;

public final class StudentAttackerController implements AttackerController
{
	private static int dangerDepth; // dangerous node distance to nearest non-vulnerable defender

	private static int safetyDepth;	// safe node distance from nearest non-vulnerable defender to pellet collect

	private static int runs;	// runs of update (used in determining direction)

	private enum AttackerStates {	// states of the attacker
			RUN,	// flee the defenders until out of danger range
			STROLL,
		SEEK_POWER,	// seek a power pill
		HUNT,		// hunt the defenders
		POPPING_PILLS,	// prioritize paths with pills
	}

	private Node target;	// target node object

	private boolean towards;	// target direction boolean

	private boolean requiresAction;	// reached a junction, need to make new decision boolean

	// state of attacker
	private AttackerStates currentState;

	public void init(Game game) {

		// initialize game behavior parameters
		dangerDepth = 10;
		safetyDepth = 30;

		// begin game variables
		towards = true;
		requiresAction = false;

		// initalize state -- get pills
		currentState = AttackerStates.POPPING_PILLS;
	}


	// seeks the next node
	private Node targetPill(Game game, Node previousTarget) {
		if (game.getAttacker().getNextDir(game.getAttacker().getTargetNode(game.getCurMaze().getPillNodes(), true), true)
		 == game.getAttacker().getReverse() && !game.checkPill(game.getAttacker().
				getTargetNode(game.getCurMaze().getPillNodes(), true))) {
			return previousTarget;
		}
		else {
			return game.getAttacker().getTargetNode(game.getCurMaze().getPillNodes(), true);
		}
	}

	// returns the nearest vulnerable defender actor object
	private Actor targetVulnerable(Game game) {
		boolean clean = false;
		List<Defender> temp = game.getDefenders();

		while (!clean) {
			for (int i = 0; i < temp.size(); i++) {
				if (!temp.get(i).isVulnerable()) {
					clean = false;
					temp.remove(i);
					break;
				}
				else {
					clean = true; // removed all non-vulnerable defenders
				}

			}
			if (temp.size() <= 0) {
				clean = true;
			}
		}
		// find the nearest
		return temp.size() > 0 ? game.getAttacker().getTargetActor(temp, true) : null;
	}

	// returns the nearest vulnerable defender actor object
	private Actor targetDangerous(Game game) {
		boolean clean = false;
		List<Defender> temp = game.getDefenders();

		while (!clean) {
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).isVulnerable()) {
					clean = false;
					temp.remove(i);
					break;
				}
				else {
					clean = true; // removed all vulnerable defenders
				}

			}
			if (temp.size() <= 0) {
				clean = true;
			}
		}
		// find the nearest
		return temp.size() > 0 ? game.getAttacker().getTargetActor(temp, true) : null;
	}

	// overloaded function for reduced lists outside of game
	private Actor targetDangerous(List<Defender> def, Attacker att) {
		boolean clean = false;
		List<Defender> temp = def;

		while (!clean) {
			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).isVulnerable()) {
					clean = false;
					temp.remove(i);
					break;
				}
				else {
					clean = true; // removed all vulnerable defenders
				}

			}
			if (temp.size() <= 0) {
				clean = true;
			}
		}
		// find the nearest
		return temp.size() > 0 ? att.getTargetActor(temp, true) : null;
	}

	// tell the program if there are more than one defenders trying to box the attacker in
	// avoid edges for higher chance of escape
	private boolean avoidEdges(Game game) {
		double temp = game.getAttacker().getLocation().getPathDistance(targetDangerous(game).getLocation());

		List<Defender> tempList = game.getDefenders();
		tempList.remove(targetDangerous(game));	// remove the most dangerous target

		// make temp a ratio
		temp = temp / game.getAttacker().getLocation().getPathDistance(targetDangerous(tempList, game.getAttacker()).getLocation());

		if (temp < 1.3 || temp > 0.7) {
			return true;
		}
		else {
			return false;
		}
	}

	public void shutdown(Game game) { }


	// method containing a definition for logical decisions
	public int update(Game game,long timeDue)
	{
		int action = Game.Direction.EMPTY; // assume the action is empty
		List<Integer> possibleDirs = game.getAttacker().getPossibleDirs(true);
		Node vulnerable;
		Node dangerous;

		// determine the state

		if (targetVulnerable(game) != null){
			currentState = AttackerStates.HUNT;
		}
		else {
			currentState = AttackerStates.POPPING_PILLS;
		}

		if (targetDangerous(game) != null) {
			if (game.getAttacker().getLocation().getPathDistance(targetDangerous(game).getLocation()) == 0) {
				currentState = AttackerStates.POPPING_PILLS;
			}
			if (game.getLevelTime() > 50 &&
					game.getAttacker().getLocation().getPathDistance(targetDangerous(game).getLocation()) < dangerDepth){
				currentState = AttackerStates.RUN;}
		}

		// change an action due to a change in state
		System.out.println(currentState);

		switch (currentState) {
			case POPPING_PILLS:
				towards = true;
				if (game.getAttacker().getLocation().getNeighbor(game.getAttacker().getDirection()) != null) {
					target = targetPill(game, game.getAttacker().getLocation().getNeighbor(game.getAttacker().getDirection()));
				}
				else {
					target = targetPill(game, game.getAttacker().getLocation().
							getNeighbor(game.getAttacker().getPossibleDirs(false).get(0)));
				}
				break;
			case RUN:
				towards = false;
				target = targetDangerous(game).getLocation();
				break;
			case HUNT:
				towards = true;
				target = targetVulnerable(game).getLocation();
				break;
			default: //REMOVE
				break;


		}
		action = game.getAttacker().getNextDir(target, towards);

		return action;
	}
}