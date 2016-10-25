package zhaos.spaceagegame.game.resources;

/**
 * Game Action handles passing of information of a game action requested by a Team Controller
 *
 *
 */
public abstract class GameAction {
    int teamID;
    public enum Instruction{
        addToStack,


    }
    abstract Instruction getInstruction();




}
