package zhaos.spaceagegame.game;

/**
 * Game Action handles passing of information of a game action requested by a Team Controller
 *
 *
 */
abstract class GameAction {
    int teamID;
    public enum Instruction{
        move,

    }
    abstract Instruction getInstruction();




}
