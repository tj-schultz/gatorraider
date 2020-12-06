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
	private static int pelletPathDepth;
	// depth of a given path to compare remaining pellets

	private static float idealPelletRatio;	// describes the decisive ratio of
	// (num pellets in palletPathDepth) / (remaining pellets)

	public void init(Game game) {

		// initialize game behavior parameters
		pelletPathDepth = 7;
		idealPelletRatio = 0.15f;

	}

	public void shutdown(Game game) { }

	public int update(Game game,long timeDue)
	{
		int action = Game.Direction.EMPTY;

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
		for (int i = 0; i < possibleDirs.size(); i++) {
			if (possibleDirs.get(i) == Game.Direction.LEFT) {
				action = Game.Direction.DOWN;
				break;
			}
			else {
				action = Game.Direction.LEFT;
			}
		}



		return action;
	}
}