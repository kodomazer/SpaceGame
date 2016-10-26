package zhaos.spaceagegame.game.resources;

/**
 * Created by kodomazer on 10/22/2016.
 */

public class RequestConstants {

    public static final String SUCCESS = "success";
    //Variable Types
    public final static String INSTRUCTION = "INSTRUCTION";
    public final static String ORIGIN_HEX = "startHex";
    public final static String ORIGIN_SUBSECTION = "startSubsection";
    public final static String DESTINATION_HEX = "endHex";
    public static final String DESTINATION_SUBSECTION = "end Subsection";


    //IDs
    public static final String UNIT_ID = "unit ID";
    public static final String CONSTRUCTION_POD_ID = "pod ID";
    public static final String STATION_ID = "city ID";

    public static final String FACTION_ID = "team";

    public static final String LEVEL = "level";

    //Hex Info
    public static final String SUBSECTION_LIST = "subsections";
    //Game Info
    public final static int GAME_INFO   =   0x0000;
    //Unit Actions
    public final static int UNIT_INFO   =   0x0100;
    public final static int UNIT_MOVE   =   0x0101;

    public final static int UNIT_SELECT =   0x0102;

    public final static int UNIT_ATTACK =   0x0103;
    //Hex Actions
    public final static int HEX_INFO    =   0x0200;
}
