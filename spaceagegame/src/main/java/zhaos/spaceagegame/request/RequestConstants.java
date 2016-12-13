package zhaos.spaceagegame.request;

import zhaos.spaceagegame.util.GameAction;

/**
 * Created by kodomazer on 10/22/2016.
 */

public class RequestConstants {

    public static final String SUCCESS = "success";
    //Variable Types
    public final static String INSTRUCTION = "INSTRUCTION";
    public final static String HEX = "startHex";
    public final static String SUBSECTION = "startSubsection";
    public final static String DESTINATION_HEX = "endHex";
    public static final String DESTINATION_SUBSECTION = "end Subsection";
    public final static String ORIGIN_HEX = "startHex";
    public final static String ORIGIN_SUBSECTION = "startSubsection";

    //IDs
    public static final String UNIT_ID = "unit ID";
    public static final String SPACE_STATION_ID = "city ID";
    public static final String FACTION_ID = "team";
    public static final String LEVEL = "level";

    //Game Info
    public static final String ACTIVE_FACTION = "faction active";


    //Hex Info
    public static final String SUBSECTION_LIST = "subsections";
    public static final String SPACE_STATION_INFO = "space station info";

    //City Info
    public static final String UNIT_LIST = "units";
    public static final String CITY_INFORMATION = "city info";

    //Unit Info
    public final static String UNIT_STATUS_FLAGS = "unit status";
    //status flags
    public final static int MOVABLE = 0b0001;
    public final static int CAN_ATTACK = 0b0010;
    public final static int SELECTED = 0b0100;

    //First level of delegation
    public static final int ACTION_MASK = 0xF000;
    public static final int GAME_ACTION = 0x1000;
    public static final int MAP_ACTION = 0x2000;
    public static final int ENTITY_ACTION = 0x3000;

    //Game Info
    public final static int GAME_INFO = GAME_ACTION | 1;
    public final static int GAME_END = GAME_ACTION | 2;
    public final static int END_TURN = GAME_ACTION | 3;

    //Hex Actions
    public final static int HEX_INFO = MAP_ACTION | 1;
    public static final int SUBSECTION_INFO = MAP_ACTION | 2;

    //Entity Actions
    public final static int UNIT_ATTACK = ENTITY_ACTION | 1;
    public final static int UNIT_MOVE = ENTITY_ACTION | 2;
    public final static int UNIT_INFO = ENTITY_ACTION | 3;
    //City Actions
    public final static int CITY_INFO = ENTITY_ACTION | 4;
    public final static int CITY_PROD_UNIT = ENTITY_ACTION | 5;
    public final static int CITY_PROD_POD = ENTITY_ACTION | 6;
}
