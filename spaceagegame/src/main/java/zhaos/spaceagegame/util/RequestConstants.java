package zhaos.spaceagegame.util;

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
        public final static int MOVEABLE    =   0b0001;
        public final static int CAN_ATTACK  =   0b0010;
        public final static int SELECTED    =   0b0100;

    //Game Info
    public final static int GAME_INFO       =   0x0000;

    //Hex Actions
    public final static int HEX_INFO        =   0x0010;
    public static final int SUBSECTION_INFO =   0x0020;

    //Unit Actions
    public final static int UNIT_INFO       =   0x0100;
    public final static int UNIT_MOVE       =   0x0101;
    public final static int UNIT_SELECT     =   0x0102;
    public final static int UNIT_ATTACK     =   0x0103;

    //City Actions
    public final static int CITY_INFO       =   0x0200;

    //Construction Pods
    public final static int CON_POD_INFO    =   0x0300;
}
