package com.glhf.bomberball.ai;

import java.util.Vector;

import com.glhf.bomberball.ai.golf.FirstAI;
import com.glhf.bomberball.ai.golf.OpponentAI;
import com.glhf.bomberball.ai.golf.alphaBetaProfondeur;
import com.glhf.bomberball.config.GameMultiConfig;
import com.glhf.bomberball.maze.cell.Cell;


public class FactoryMethod {

    Vector<String> vectAi;


    public FactoryMethod()
    {
        this.vectAi = new Vector<String>();
        this.vectAi.add("Bob");
        this.vectAi.add("Alice");

    }


    public AbstractAI getAI(String name, GameMultiConfig configMultiPlayers, String player_skin, Cell cell,int playerId) {
        try
        {
            switch (name) {
                case "RandomAI":
                    AbstractAI player=new RandomAI(configMultiPlayers,player_skin,playerId);
                    cell.addGameObject(player);
                    return player;

                case "VanillaAI":
                    AbstractAI player1=new VanillaAI(configMultiPlayers,player_skin,playerId);
                    cell.addGameObject(player1);
                    return player1;

                case "FirstAI":
                    AbstractAI player2=new FirstAI(configMultiPlayers,player_skin,playerId);
                    cell.addGameObject(player2);
                    return player2;

                case "OpponentAI":
                    AbstractAI player3=new OpponentAI(configMultiPlayers,player_skin,playerId);
                    cell.addGameObject(player3);
                    return player3;

                case "alphaBetaProfondeur":
                    AbstractAI player4=new alphaBetaProfondeur(configMultiPlayers,player_skin,playerId);
                    cell.addGameObject(player4);
                    return player4;

                default:
                    AbstractAI playerDefault=new RandomAI(configMultiPlayers,player_skin,playerId);
                    cell.addGameObject(playerDefault);
                    return playerDefault;


            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        return null;
    }








    public Vector<String> getVectAi()
    {
        return vectAi;
    }




}
