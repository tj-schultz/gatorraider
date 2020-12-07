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

	private enum AttackerStates {	// states of the attacker
			RUN,	// flee the defenders until out of danger range
		HUNT,		// hunt the defenders
		POPPING_PILLS,	// prioritize paths with pills
	}

	private Node target;	// target node object

	private boolean towards;	// target direction boolean


	// state of attacker
	private AttackerStates currentState;

	public void init(Game game) {

		// initialize game behavior parameters
		dangerDepth = 5;//5 is magic

		// begin game variables
		towards = true;

		// initalize state -- get pills
		currentState = AttackerStates.POPPING_PILLS;
	}


	// seeks the next pill node if in the POPPING_PILLS state
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

	// overloaded function for reduced lists outside of game class
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

	public void shutdown(Game game) { }


	// method containing a definition for logical decisions
	public int update(Game game,long timeDue)
	{
		int action = Game.Direction.EMPTY; // assume the action is empty
		List<Integer> possibleDirs = game.getAttacker().getPossibleDirs(true);

		// determine the state

		// if there is a vulnerable attacker, hunt it, else collect pills
		if (targetVulnerable(game) != null){
			currentState = AttackerStates.HUNT;
		}
		else {
			currentState = AttackerStates.POPPING_PILLS;
		}

		// if there is a non-vulnerable defender in the maze
		if (targetDangerous(game) != null) {
			if (game.getAttacker().getLocation().getPathDistance(targetDangerous(game).getLocation()) == 0) {
				currentState = AttackerStates.POPPING_PILLS;
			}

			// if the threat of a defender is close enough to change state
			if (game.getLevelTime() > 50 &&
					game.getAttacker().getLocation().getPathDistance(targetDangerous(game).getLocation()) < dangerDepth){
				currentState = AttackerStates.RUN;}
		}

		// determine a target node and directional preference from the state
		switch (currentState) {
			case POPPING_PILLS:
				towards = true;

				// if the direction exists
				if (game.getAttacker().getLocation().getNeighbor(game.getAttacker().getDirection()) != null) {
					target = targetPill(game, game.getAttacker().getLocation().getNeighbor(game.getAttacker().getDirection()));
				}
				else {
					target = targetPill(game, game.getAttacker().getLocation().
							getNeighbor(game.getAttacker().getPossibleDirs(false).get(0)));
							// use a default movement
				}
				break;
			case RUN:	// move anti to the defenders
				towards = false;
				target = targetDangerous(game).getLocation();
				break;
			case HUNT:	// go for the vulnerable defenders
				towards = true;
				target = targetVulnerable(game).getLocation();
				break;

		}

		// return action from the target node and directional preference
		action = game.getAttacker().getNextDir(target, towards);

		return action;
	}
}