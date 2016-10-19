package zhaos.spaceagegame.game;

import android.graphics.Point;

import zhaos.spaceagegame.ui.SubsectionGroup;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Team Control Interface is the main interface for the UI of any given player
 * (Or an AI) to interact with the game.
 *
 * The interface includes:
 *      Selecting Tiles and Units
 *      Getting additional Text and visual info for selected Tiles and Units
 *      Making actions with selected Units
 *
 */
//TODO: Annotate all the methods
public interface TeamControlInterface {
    //Selection
    //Map Selection
    void selectHex(Point hex);
    void selectSubsection(Point hex, HHexDirection subsection);

    //Unit Selection
    void selectUnit(int unitID);
    void selectUnit(int[] unitIDs);
    void addToSelection(int unitID);
    void addToSelection(int[] unitIDs);
    void deselectUnits();
    void deselectSubsection();

    //City Selection
    void selectCity(int cityID);




    //Info
    //MetaInfo
    int teamID();
    //Map Info
    UnitData[] unitsInSubsection();
    class UnitData{
        //0: City
        //1: Construction Pod
        //2: Units
        int type;
        //ID of the unit, for internal use
        int id;
        //Level of the Unit or City, Construction Pods are always level 1
        int level;

    }
    //Hex Info
    HexStatus selectedHexStatus();
    HexStatus[] mapHexStatus();
    class HexStatus{
        Point Hex;
        int owner;
        int[] averageInfluence;
    }
    //Subsection Info
    SubsectionStatus[] mapSubsectionStatus();
    class SubsectionStatus{
        Point hex;
        HHexDirection subsection;
        int owner;
        int[] influenceLevels;
    }
    SubsectionMoveStatus[] moveView();
    class SubsectionMoveStatus{
        Point hex;
        HHexDirection subsection;
        int move;
    }
    //Unit Info

    //Construction Pod Info
    int actions();

    //City Info

    //Actions
    //Unit Action
    boolean moveToSubsection(Point hex,HHexDirection subsection);
    boolean attackSubsection(Point hex,HHexDirection subsection);
    boolean useConstructionPod(int podID);
    boolean pickUp(int podID);

    /**
     * Drops held Construction Pod if the selected unit is holding a construction pod
     * @return true: if held construction pod was successfully dropped on the current subsection
     * false: if there the action was unable to go through
     */
    boolean drop();

    //City Action
    boolean harvestEnergy(Point hex);
    boolean harvestEnergy(Point hex,int amount);
    boolean produceUnit();
    boolean produceConstructionPod();
    boolean upgradeUnit(int unitID);
}
