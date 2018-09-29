package com.galaxy_craft.ribosome;

public class Texts {
    //configuration file comments
    public static final String CONFIG_ROOT_COMMENT = "Configuration for Ribosome Sponge plugin by Brycey92";
    public static final String CONFIG_DEFAULT_AMOUNT_COMMENT = "The amount of items to give when there is no amount specified in the command";
    public static final String CONFIG_OVERFLOW_ACTION_COMMENT = "The action to take when the requested items overflow the inventory. 0 = " + getOverflowActionText(0) + ", 1 = " + getOverflowActionText(1);
    public static final String CONFIG_MAX_STACKS_COMMENT = "The maximum number of stacks to drop at a player's feet when their inventory is full. Only used when set to " + getOverflowActionText(1).toLowerCase() + ".";

    //configuration statuses
    public static final String LOAD_IO_ERROR = "Error loading configuration! Falling back to default amount of " + Ribosome.DEFAULT_DEFAULT_AMOUNT
            + " and default overflow action of " + getOverflowActionText(Ribosome.DEFAULT_OVERFLOW_ACTION).toLowerCase() + ".";
    public static final String LOAD_TYPE_ERROR = "";
    public static final String SAVE_IO_ERROR = "Error saving configuration!";
    public static final String RELOAD_CONFIG_SUCCESS = "Ribosome's configuration was reloaded successfully.";

    //event-based statuses
    public static final String REGISTERED_COMMANDS = "Registered commands.";
    public static final String SERVER_START = "Ready to make some proteins!";

    //command descriptions
    public static final String COMMAND_RIBOSOME_DESCRIPTION = "Provides information on Ribosome's commands.";
    public static final String COMMAND_RELOAD_DESCRIPTION = "Reloads Ribosome's configuration.";
    public static final String COMMAND_ITEM_DESCRIPTION = "Gives items to the player.";
    public static final String COMMAND_GIVE_DESCRIPTION = "Gives items to a specified player.";

    public static String getOverflowActionText(int overflowAction) {
        switch (overflowAction) {
            case 0:
                return "Ignore leftovers";
            case 1:
                return "Drop leftovers";
            default:
                return "[Error: No description for overflow action " + overflowAction + ".]";
        }
    }
}
