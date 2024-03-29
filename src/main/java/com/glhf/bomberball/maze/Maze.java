package com.glhf.bomberball.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.glhf.bomberball.config.GameSoloConfig;
import com.glhf.bomberball.utils.Constants;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.config.GameMultiConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.json.GameObjectTypeAdapter;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.VectorInt2;
import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Maze implements Cloneable{

    String title;
    List<VectorInt2> spawn_positions = new ArrayList<>();

    int height;
    int width;
    Cell[][] cells;
    List<Player> players;
    static Gson gson;

    public Maze() {
    }

    public Maze(int w, int h) {
        this.height = h;
        this.width = w;
        cells = new Cell[w][h];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }
        initialize();
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }


    public Object clone() {
        Maze mazeClone = new Maze(this.width, this.height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                mazeClone.cells[x][y] = this.cells[x][y].clone();
            }
        }

        mazeClone.spawn_positions= new ArrayList<VectorInt2>(spawn_positions.size());
        for (int i = 0; i < players.size(); i++) {
            mazeClone.spawn_positions.add(((VectorInt2) spawn_positions.get(i)));
        }

        // Browse all cell clones and set players as well as cell adjacency
        mazeClone.initialize();

        mazeClone.players = new ArrayList<Player>(players.size());
        // Initialize list
        for (int i = 0; i < players.size(); i++) {
            mazeClone.players.add(null);
        }
        // Set real values
        for (int i = 0; i < players.size(); i++) {
            Cell playerCell = players.get(i).getCell();
            Cell playerCloneCell = mazeClone.getCellAt(playerCell.getX(), playerCell.getY());
            for (Player p : playerCloneCell.getInstancesOf(Player.class)) {
                int id = p.getPlayerId();
                mazeClone.players.set(id, p);
            }
        }

        return mazeClone;

    }
    public List<VectorInt2> getSpawn_positions() {
        return spawn_positions;
    }

    public void setSpawn_positions(ArrayList<VectorInt2> spawn_positions) {
        this.spawn_positions = spawn_positions;
    }





    public void initialize()
    {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y].initialize(getCellAt(x+1,y), getCellAt(x,y+1), getCellAt(x-1,y), getCellAt(x,y-1));
            }
        }
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Player spawnPlayer(GameSoloConfig config)
    {
        VectorInt2 pos = spawn_positions.get(0);
        return spawnPlayer(config, config.player_skin, cells[pos.x][pos.y]);
    }

    public List<Player> spawnPlayers(int nb_player)
    {
        GameMultiConfig config = GameMultiConfig.get();
        players = new ArrayList<>();
        for (int i = 0; i < nb_player; i++) {
            VectorInt2 pos = spawn_positions.get(i);
            Player player = spawnPlayer(config, config.player_skins[i], cells[pos.x][pos.y]);
            players.add(player);
        }
        return players;
    }

    public void spawnPlayers(List<Player> players)
    {
        this.players = players;
        int n = players.size();
        for (int i = 0; i < n; i++) {
            VectorInt2 pos = spawn_positions.get(i);
            Player player = players.get(i);
            setPlayer(player, cells[pos.x][pos.y]);
        }
    }

    private Player spawnPlayer(GameConfig config, String player_skin, Cell cell) {
        Player player = new Player(
                player_skin,
                config.player_life,
                config.initial_player_moves,
                config.initial_bomb_number,
                config.initial_bomb_range);
        cell.addGameObject(player);
        return player;
    }

    private void setPlayer(Player player, Cell cell) {
        player.setCell(cell);
    }


    public List<VectorInt2> getPlayersSpawns() {
        return spawn_positions;
    }

    /**
     * Getter for the variable height
     * @return an integer
     */
    public int getHeight() {
        return height;
    }

    /**
     * Getter for the variable width
     * @return an integer
     */
    public int getWidth() {
        return width;
    }

    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Detonates all the bombs in the Maze and thus delete them from the bombs ArrayList
     */
    public void processEndTurn()
    {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y].processEndTurn();
            }
        }
    }

    public Cell getCellAt(int x, int  y) {
        if(isCellInBounds(x, y))
            return cells[x][y];
        return null;
    }

    /**
     * tests if a given position is in the maze
     * @param cell_x
     * @param cell_y
     * @return true if the position is in the maze, else it returns false
     */
    public boolean isCellInBounds(int cell_x, int cell_y)
    {
        return cell_x >= 0 && cell_x < width && cell_y >= 0 && cell_y < height;
    }

    public ArrayList<Enemy> getEnemies() {
        ArrayList<Enemy> enemies = new ArrayList<>();
        for(Cell[] cell_column : cells){
            for(Cell cell : cell_column){
                for(GameObject o : cell.getGameObjects()){
                    if(o instanceof Enemy) enemies.add((Enemy) o);
                }
            }
        }
        return enemies;
    }

    public void setPlayerSpawns(ArrayList<VectorInt2> spawns)
    {
        spawn_positions = spawns;
    }

    private static void createGson() {
        gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(GameObject.class, new GameObjectTypeAdapter())
                .setPrettyPrinting()
                .create();
    }

    public static Maze importMazeSolo(String name) {
        FileHandle fh = Gdx.files.internal(Constants.PATH_MAZE + "solo/" + name + ".json");
        return importMaze(fh);
    }

    public static Maze importMazeMulti(String name) {
        FileHandle fh = getFileHandleMulti(name);
        return importMaze(fh);
    }

    private static Maze importMaze(FileHandle fh) {
        if(gson==null) {
            createGson();
        }
        if(fh == null) System.err.println("file maze not found ...");
        Maze maze = gson.fromJson(fh.readString(), Maze.class);
        maze.initialize();
        return maze;
    }

    private static FileHandle getFileHandleMulti(String name) {
        FileHandle fh = Gdx.files.local(Constants.PATH_MAZECUSTOM+ name + ".json");
        if(!fh.exists()){
            fh = Gdx.files.internal(Constants.PATH_MAZE+ "multi/" + name + ".json");
            if(!fh.exists()){
                fh = null;
            }
        }
        return fh;
    }

    public static int countMazesMulti() {
        int i;
        for(i = 0; getFileHandleMulti("maze_"+i) != null; i++);
        return i;
    }

    public void exportMaze(String name) {
        File file = Gdx.files.internal(Constants.PATH_MAZE + name + ".json").file();
        writeToFile(file);
    }

    public void exportCustomMaze(String name) {
        File file = Gdx.files.local(Constants.PATH_MAZECUSTOM + name + ".json").file();
        if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
        writeToFile(file);
    }

    private void writeToFile(File file){
        if(gson == null) createGson();

        try {
            Writer writer = new FileWriter(file);
            writer.write(this.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }









    @Override
    public String toString() {
        return gson.toJson(this);
    }

}
