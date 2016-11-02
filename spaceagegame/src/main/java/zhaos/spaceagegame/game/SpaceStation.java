package zhaos.spaceagegame.game;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class SpaceStation{

    private int ID;
    private int level;
    private int actions;
    private int affiliation;

    private SpaceGameHexTile hexTile;

    SpaceStation(int faction,SpaceGameHexTile hexTile,int ID){
        level = 1;
        this.affiliation = faction;
        this.hexTile = hexTile;
        this.ID = ID;
    }

    public int getAffiliation(){
        return affiliation;
    }

    public SpaceGameHexTile getHexTile(){
        return hexTile;
    }


    public int getID() {
        return ID;
    }

    public int getLevel() {
        return level;
    }
}
