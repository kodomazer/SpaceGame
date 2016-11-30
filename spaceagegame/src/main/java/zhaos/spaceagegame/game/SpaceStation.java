package zhaos.spaceagegame.game;

import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class SpaceStation{

    private int ID;
    private int level;
    private int actions;
    private int affiliation;

    private SpaceGameHexTile hexTile;
    private SpaceGameLocal parentGame;

    SpaceStation(int faction,SpaceGameHexTile hexTile,int ID){
        level = 1;
        this.affiliation = faction;
        this.hexTile = hexTile;
        this.ID = ID;

        parentGame = SpaceGameLocal.getInstance();
    }

    public int getAffiliation(){
        return affiliation;
    }

    public SpaceGameHexTile getHexTile(){
        return hexTile;
    }

    public void createUnit(){
        Unit unit = new Unit(this);
        parentGame.registerUnit(unit);
        hexTile.getSubsection(HHexDirection.CENTER).moveIn(unit);
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
