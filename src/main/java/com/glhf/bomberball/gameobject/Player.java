package com.glhf.bomberball.gameobject;

import com.glhf.bomberball.maze.cell.Cell;

import java.util.ArrayList;
import java.util.Observer;

import com.glhf.bomberball.utils.Directions;

public class Player extends Character {

    private ArrayList<Observer> observers;
    protected boolean active;
    protected String name;

    protected int initial_bomb_number;
    protected int initial_bomb_range;

    protected transient int bombs_remaining;

    public transient int bonus_bomb_number = 0;
    public transient int bonus_bomb_range = 0;
    public transient int bonus_moves = 0;
    protected int player_id;

    public Player(String player_skin,
                  int life,
                  int initial_moves,
                  int initial_bomb_number,
                  int initial_bomb_range)
    {
        super(player_skin, life, initial_moves);
        this.observers = new ArrayList<>();
        this.active = false;
        this.name = "Human";
        this.initial_bomb_number = initial_bomb_number;
        this.initial_bomb_range = initial_bomb_range;
        initialize();
    }

    public Player(String player_skin,
                  int life,
                  int initial_moves,
                  int initial_bomb_number,
                  int initial_bomb_range,
                  String name)
    {
        super(player_skin, life, initial_moves);
        this.observers = new ArrayList<>();
        this.active = false;
        this.name = name;
        this.initial_bomb_number = initial_bomb_number;
        this.initial_bomb_range = initial_bomb_range;
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    /**
     * initiate the turn of the player, initialize the number of bombs and the number of moves
     */
    @Override
    public void initiateTurn() {
        active = true;
        moves_remaining = initial_moves + bonus_moves;
        bombs_remaining = initial_bomb_number + bonus_bomb_number;
        this.notifyObservers();
    }

    @Override
    public void endTurn() {
        active = false;
        this.notifyObservers();
    }

    @Override
    public boolean move(Directions dir)
    {
        if (super.move(dir)) {
            for (GameObject go : cell.getGameObjects()) {
                if (go instanceof Enemy) {
                    ((Enemy) go).touchPlayer(this);
                }
            }
            this.notifyObservers();
            return true;
        }
        return false;
    }

    /**
     * The player create a bomb and put it on the square given
     * @param dir
     * @return if the bomb has been dropped
     */
    public boolean dropBomb(Directions dir) {
        boolean dropped = false;
        if (bombs_remaining > 0) {
            Cell dest_cell = cell.getAdjacentCell(dir);
            if (dest_cell != null && dest_cell.isWalkable()) {
                bombs_remaining--;
                Bomb bomb = new Bomb(1, initial_bomb_range + bonus_bomb_range);
                dest_cell.addGameObject(bomb);
                this.notifyObservers();
                dropped = true;
            }
        }
        return dropped;
    }

    @Override
    public void interactWithCell(Cell cell) {
        for (Bonus bonus : cell.getInstancesOf(Bonus.class)) {
            bonus.dispose();
            bonus.applyEffect(this);
        }
    }

    public int getNumberBombRemaining() {
        return bombs_remaining;
    }

    public int getNumberMoveRemaining() {
        return moves_remaining;
    }

    public int getBombRange() {
        return bonus_bomb_range+initial_bomb_range;
    }

    public boolean isActive() {
        return active;
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    private void notifyObservers() {
        for (Observer observer : this.observers) {
            observer.update(null, this);
        }
    }

    @Override
    public GameObject clone() {
        Player clone = new Player(skin, life, initial_moves, initial_bomb_number, initial_bomb_range, name);
        clone.sprite = this.sprite;
        clone.cell = this.cell;

        clone.moves_remaining = this.moves_remaining;

        clone.active = this.active;
        clone.bombs_remaining = this.bombs_remaining;

        clone.bonus_bomb_number = this.bonus_bomb_number;
        clone.bonus_bomb_range = this.bonus_bomb_range;
        clone.bonus_moves = this.bonus_moves;
        clone.player_id = this.player_id;

        return clone;
    }

    public int getPlayerId() {
        return player_id;
    }
    public void setPlayerId(int playerId) {
        this.player_id=playerId;

    }

    @Override
    public String toString() {
        return "Player [ name=" + name + ", bombs_remaining="
                + bombs_remaining + ", player_id=" + player_id + ", moves_remaining=" + moves_remaining + ", life="
                + life + "]";
    }


}

