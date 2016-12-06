package zhaos.spaceagegame.spaceGame;

import java.util.ArrayList;
import java.util.Collection;

import zhaos.spaceagegame.spaceGame.entity.SpaceStation;
import zhaos.spaceagegame.spaceGame.entity.Unit;

/**
 * Created by kodomazer on 9/26/2016.
 */
public class TeamController {
    Collection<Unit> units;
    Collection<SpaceStation> cities;


    TeamController(){
        units = new ArrayList<>();
        cities = new ArrayList<>();
    }

    public Collection<Unit> getUnits(){
        return  units;
    }

    public Collection<SpaceStation> getCities(){
        return cities;
    }



}
