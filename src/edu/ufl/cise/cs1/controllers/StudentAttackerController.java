// Tyler J. Schultz
// COP3502
// StudentAttackerController Class

package edu.ufl.cise.cs1.controllers;
import game.controllers.AttackerController;
import game.models.*;
import java.awt.*;
import java.util.List;

public final class StudentAttackerController implements AttackerController
{
	private static int dangerDepth; // dangerous node distance to nearest non-vulnerable defender

	private static int safetyDepth;	// safe node distance from nearest non-vulnerable defender to pellet collect

	private enum AttackerStates {	// states of the attacker
			RUN,
		SEEK_POWER,
		HUNT,
		POPPING_PILLS,
		NULLSTATE
	}

	// state of attacker
	private AttackerStates currentState;

	public void init(Game game) {

		// initialize game behavior parameters
		dangerDepth = 4;
		safetyDepth = 20;

		// initalize state -- get pills
		currentState = AttackerStates.POPPING_PILLS;
	}

	public void shutdown(Game game) { }

	public int update(Game game,long timeDue)
	{
		int action = Game.Direction.EMPTY;

		// get the position and check state
		/*
		List<Integer> possibleDirs = game.getAttacker().getPossibleDirs(true);
		if (possibleDirs.size() != 0)
			action = possibleDirs.get(Game.rng.nextInt(possibleDirs.size()));
		else
			action = -1;

		//An example (which should not be in your final submission) of some syntax to use the visual debugging method, addPathTo, to the top left power pill.
		List<Node> powerPills = game.getPowerPillList();
		if (powerPills.size() != 0) {
			game.getAttacker().addPathTo(game, Color.BLUE, powerPills.get(0));
		}*/


		// code to win as gator
		List<Integer> possibleDirs = game.getAttacker().getPossibleDirs(true);
		/*
		for (int i = 0; i < possibleDirs.size(); i++) {
			if (possibleDirs.get(i) == Game.Direction.LEFT) {
				action = Game.Direction.DOWN;
				break;
			}
			else {
				action = Game.Direction.LEFT;
			}
		}
		*/

		// EMPLOY FUZZY LOGIC



		return action;
	}
}