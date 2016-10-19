package zhaos.spaceagegame.game;

import java.util.ArrayList;
import java.util.Collection;

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
