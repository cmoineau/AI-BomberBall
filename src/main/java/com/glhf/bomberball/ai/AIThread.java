package com.glhf.bomberball.ai;

import java.util.concurrent.ExecutorService;

import com.glhf.bomberball.utils.Action;




public class AIThread extends Thread {


	/**
	 * Joueur artificiel
	 */
	private final AbstractAI ai;

	/**
	 * Partie en cours
	 */
	private final GameState gameState;

	/**
	 * Service d'execution du thread
	 */
	private final ExecutorService executor;

	/**
	 * Coup choisi a l'issu de la recherche
	 */
	private Action choosedAction;

	/**
	 * Constructor
	 * @param ai  Artificial Player
	 * @param gameState  represent  game state
	 * @param executor Service
	 */
	public AIThread (AbstractAI ai, GameState gameState, ExecutorService executor) {
		super("Calcul");
		setName("Calcul");
		this.ai = ai;
		this.gameState = gameState;
		this.executor = executor;
		this.choosedAction = null;
	}

	public Action getChoosedAction() {
		return choosedAction;
	}

	/**
	 * Lance la recherche d'un nouveau coup dans un thread separe
	 */
	@Override
	public void run() {
		try {
			choosedAction = ai.choosedAction((GameState) gameState.clone());
		}
		catch (Exception ex) {

			executor.shutdownNow();
		}
	}








}
