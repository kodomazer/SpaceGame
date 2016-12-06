package zhaos.spaceagegame.spaceGame.entity;

import zhaos.spaceagegame.spaceGame.LocalGame;
import zhaos.spaceagegame.spaceGame.entity.Unit;
import zhaos.spaceagegame.spaceGame.map.HexTile;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class SpaceStation{

    private int ID;
    private int level;
    private int actions;
    private int affiliation;

    private HexTile hexTile;
    private LocalGame parentGame;

    SpaceStation(int faction, HexTile hexTile, int ID){
        level = 1;
        this.affiliation = faction;
        this.hexTile = hexTile;
        this.ID = ID;

        parentGame = LocalGame.getInstance();
    }

    int getAffiliation(){
        return affiliation;
    }

    HexTile getHexTile(){
        return hexTile;
    }

    public void createUnit(){
        Unit unit = parentGame.newUnit(this);
    }

    public int getID() {
        return ID;
    }

    public int getLevel() {
        return level;
    }

    public void upgrade() {
        level++;
    }
}
