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

	private static int avgClusterDepth;	// avg of active ghosts

	private enum AttackerStates {	// states of the attacker
			RUN,	// flee the defenders until out of danger range
		SEEK_POWER,	// seek a power pill
		HUNT,		// hunt the defenders
		POPPING_PILLS,	// prioritize paths with pills
		NULLSTATE		// null state
	}

	// state of attacker
	private AttackerStates currentState;

	public void init(Game game) {

		// initialize game behavior parameters
		dangerDepth = 8;
		safetyDepth = 20;
		avgClusterDepth = 30;

		// initalize state -- get pills
		currentState = AttackerStates.POPPING_PILLS;
	}

	public void shutdown(Game game) { }


	// method containing a definition for logical decisions
	public int update(Game game,long timeDue)
	{
		int action = Game.Direction.EMPTY; // assume the action is empty
		List<Integer> possibleDirs = game.getAttacker().getPossibleDirs(true);

		int distToNearestDef;
		Actor nearest = game.getAttacker().getTargetActor(game.getDefenders(), true);
		Actor farthest = game.getAttacker().getTargetActor(game.getDefenders(), false);
		distToNearestDef = nearest.getPathTo(game.getAttacker().getLocation()).size();
		System.out.println(distToNearestDef);

		// decide state
		if (distToNearestDef <= dangerDepth) {
			currentState = AttackerStates.RUN;
		}
		else if (distToNearestDef > dangerDepth && distToNearestDef <= safetyDepth){

			// make an evaluation based on the average of the closest and other defenders

			// find the relative cluster of defenders
			int numActiveDef = game.getDefenders().size();
			double avg = 0;
			for (int i = 0; i < game.getDefenders().size() - 1; i++) {
				if (game.getDefender(i).getPathTo(nearest.getLocation()).size() <= 0) {
					if (game.getDefender(i).equals(nearest)) {
						continue;	// skip, is nearest
					}
					// in lair
					numActiveDef--;
					continue;
				}
				else {

					// add the path length
					avg += game.getDefender(i).getPathTo(nearest.getLocation()).size();
				}
			}

			// calculate the average

			avg = avg / numActiveDef;

			if (avg > avgClusterDepth) {
				currentState = AttackerStates.POPPING_PILLS;	// continue finding pills
			}
			else {
				currentState = AttackerStates.SEEK_POWER;		// begin moving towards a power pill
			}

		}
		else {
			currentState = AttackerStates.POPPING_PILLS;
		}

		// evaluate state, possible directions, and decide action
		switch (currentState) {
			case RUN:
				action =
						game.getAttacker().getNextDir(nearest.getLocation(), nearest.isVulnerable() ?  false);
				break;
			case POPPING_PILLS:

			case SEEK_POWER:
				List<Node> activePills = game.getPowerPillList(); // find the active power pills
				for (int i = 0; i < game.getPowerPillList().size(); i++) {
					activePills.remove(activePills.get(i));
					if (game.checkPowerPill(game.getPowerPillList().get(i))) {
						activePills.add(game.getPowerPillList().get(i));	// add an active power pill
					}
				}

				// find the nearest power pill
				if (activePills.size() <= 0) {
					currentState = AttackerStates.POPPING_PILLS;
				}
				else {
					action =
							game.getAttacker().getNextDir(game.getAttacker().
									getTargetNode(activePills, true), true);

				}
				break;

			case HUNT:
				List<Node> vulnerableDefs = null; // find the defenders
				for (int i = 0; i < game.getDefenders().size(); i++) {
					if (game.getDefenders().get(i).isVulnerable()) {
						vulnerableDefs.add(game.getDefenders().get(i).getLocation());	// add an vulnerable defender
					}
				}

				// find the nearest vulnerable defender
				action =
						game.getAttacker().getNextDir(game.getAttacker().
								getTargetNode(vulnerableDefs, true), true);

				break;

		}

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
		//List<Integer> possibleDirs = game.getAttacker().getPossibleDirs(true);
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