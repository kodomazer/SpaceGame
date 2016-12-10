package zhaos.spaceagegame.request;

/**
 * Created by kodomazer on 10/22/2016.
 */

public class RequestConstants {

    public static final String SUCCESS = "success";
    //Variable Types
    public final static String INSTRUCTION = "INSTRUCTION";
    public final static String HEX = "startHex";
    public final static String SUBSECTION = "startSubsection";
    public final static String ORIGIN_HEX = "startHex";
    public final static String ORIGIN_SUBSECTION = "startSubsection";
    public final static String DESTINATION_HEX = "endHex";
    public static final String DESTINATION_SUBSECTION = "end Subsection";


    //IDs
    public static final String UNIT_ID = "unit ID";
    public static final String CONSTRUCTION_POD_ID = "pod ID";
    public static final String SPACE_STATION_ID = "city ID";
    public static final String FACTION_ID = "team";
    public static final String LEVEL = "level";

    //Faction Info
    public static final String FACTION_INFO = "faction info";

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

    //Second level of delegation
    public static final int HANDLER_MASK = 0x0F00;
    //Map Actions
    public static final int HEX_HANDLER = 0x0100;
    public static final int SUB_HANDLER = 0x0200;
    //Entity Actions
    public static final int UNIT_HANDLER = 0x0100;
    public static final int CITY_HANDLER = 0x0200;
    public static final int POD_HANDLER = 0x0300;

    //Gives us up to 255 instructions for the final branch, I doubt it'll even hit 15
    public static final int INSTRUCTION_MASK = 0x00FF;

    //Game Info
    public final static int GAME_INFO = GAME_ACTION + 1;

    //Hex Actions
    public final static int HEX_INFO = MAP_ACTION | HEX_HANDLER | 1;
    public static final int SUBSECTION_INFO = MAP_ACTION | SUB_HANDLER | 1;

    //Entity Actions
    public final static int UNIT_SELECT = ENTITY_ACTION | 1;
    public final static int UNIT_ATTACK = ENTITY_ACTION | 2;
    public final static int UNIT_MOVE = ENTITY_ACTION | 3;
    public final static int UNIT_INFO = ENTITY_ACTION | UNIT_HANDLER |1;
    //City Actions
    public final static int CITY_INFO = ENTITY_ACTION | CITY_HANDLER | 1;
    public final static int CITY_PROD_UNIT = ENTITY_ACTION | CITY_HANDLER | 2;
    public final static int CITY_PROD_POD = ENTITY_ACTION | CITY_HANDLER | 3;
    //Construction Pods
    public final static int CON_POD_INFO = ENTITY_ACTION | POD_HANDLER | 1;
}
