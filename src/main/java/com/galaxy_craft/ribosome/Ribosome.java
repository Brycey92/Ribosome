package com.galaxy_craft.ribosome;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.Integer;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "ribosome",
        name = "Ribosome",
        description = "A small plugin to add the /item and /giveitem commands.",
        url = "http://www.galaxy-craft.com",
        authors = {
                "Brycey92"
        }
)
public class Ribosome {
    public static final int DEFAULT_DEFAULT_AMOUNT = 1;
    public static final int DEFAULT_OVERFLOW_ACTION = 0;
    public static final int DEFAULT_MAX_STACKS = 36;

    private static Ribosome instance;

    @Inject
    private Logger logger;

    @Inject
    private Game game;

    @Inject
    private PluginContainer pluginContainer;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path configPath;

    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    private static int defaultAmount, overflowAction, maxStacks;
    private boolean configChanged = false;

    public Ribosome() {
        instance = this;
    }

    public static Ribosome getInstance() {
        return instance;
    }

    //Initialization states only occur once during a single run.

    //@Plugin class instance for each plugin is triggered.
    public void onConstruction(GameConstructionEvent event) {

    }

    @Listener
    //Get ready for initialization. Access to a default logger instance and access to information regarding preferred configuration file locations is available.
    public void onPreInit(GamePreInitializationEvent event) {
        configLoader = HoconConfigurationLoader.builder().setPath(configPath).build();
        loadConfig();
    }

    @Listener
    //Finish any work needed in order to be functional. Global event handlers should getCommandSpec registered in this stage.
    public void onInit(GameInitializationEvent event) {
        Sponge.getCommandManager().register(this, ItemCommand.getCommandSpec(), "item", "i");
        Sponge.getCommandManager().register(this, GiveCommand.getCommandSpec(), "giveitem", "gi");

        logger.info(Texts.REGISTERED_COMMANDS);
    }

    //By this state, inter-plugin communication should be ready to occur. Plugins providing an API should be ready to accept basic requests.
    public void onPostInit(GamePostInitializationEvent event) {

    }

    //By this state, all plugin initialization should be completed.
    public void onLoadComplete(GameLoadCompleteEvent event) {

    }

    @Listener
    public void onReload(GameReloadEvent event) {
        loadConfig();
    }

    //Running States can occur multiple times during a single run. SERVER_ABOUT_TO_START may follow SERVER_STOPPED, and SERVER_STOPPED may occur at any point during the process if there is an error.

    //The server instance exists, but worlds are not yet loaded.
    public void onServerAboutToStart(GameAboutToStartServerEvent event) {

    }

    //The server instance exists, and worlds are loaded. Command registration is handled during this state.
    public void onServerStarting(GameStartingServerEvent event) {

    }

    //The server instance exists, and worlds are loaded.
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info(Texts.SERVER_START);
    }

    //This state occurs immediately before the final tick, before the worlds are saved.
    public void onServerStopping(GameStoppingServerEvent event) {

    }

    //During this state, no players are connected and no changes to worlds are saved.
    public void onServerStop(GameStoppedServerEvent event) {

    }

    //Stopping states are not guaranteed to be run during shutdown. They may not fire if the game is force-stopped via Ctrl-C, Task Manager, a computer crash, or similar situations.

    //This state occurs immediately before GAME_STOPPED. Plugins providing an API should still be capable of accepting basic requests.
    public void onGameStopping(GameStoppingEvent event) {

    }

    //Once this event has finished executing, Minecraft will shut down. No further interaction with the game or other plugins should be attempted at this point.
    public void onGameStop(GameStoppedEvent event) {

    }

    public static int getDefaultAmount() {
        return defaultAmount;
    }

    public static int getOverflowAction() {
        return overflowAction;
    }

    public static int getMaxStacks() { return maxStacks; }

    //load the configuration from file, fixing any missing or invalid settings and saving the file if changes are made
    protected void loadConfig() {
        CommentedConfigurationNode rootNode;
        configChanged = false;

        //load the config
        try {
            rootNode = configLoader.load();
        } catch(IOException e) {
            logger.warn(Texts.LOAD_IO_ERROR, e);
            rootNode = configLoader.createEmptyNode();
            configChanged = true;
        }

        loadConfigurationNode(rootNode, Texts.CONFIG_ROOT_COMMENT, null);

        //get the default amount node from config
        CommentedConfigurationNode defaultAmountNode = rootNode.getNode("defaultAmount");

        defaultAmount = loadConfigurationNode(defaultAmountNode, Texts.CONFIG_DEFAULT_AMOUNT_COMMENT, DEFAULT_DEFAULT_AMOUNT).get();

        //check that the default amount configured is a positive number, and reset it if not
        if(defaultAmount < 1) {
            logger.warn("Default amount was set to " + defaultAmount + " but must be > 0. Resetting to default of " + DEFAULT_DEFAULT_AMOUNT + ".");
            defaultAmountNode.setValue(DEFAULT_DEFAULT_AMOUNT);
            defaultAmount = DEFAULT_DEFAULT_AMOUNT;
            configChanged = true;
        }

        //get the overflow action from config
        CommentedConfigurationNode overflowActionNode = rootNode.getNode("overflowAction");

        overflowAction = loadConfigurationNode(overflowActionNode, Texts.CONFIG_OVERFLOW_ACTION_COMMENT, DEFAULT_OVERFLOW_ACTION).get();

        //check that the overflow action configured is one of the choices, and reset it if not
        if(overflowAction != 0 && overflowAction != 1) {
            logger.warn("Overflow action was set to " + overflowAction + " but must be 0 or 1. Resetting to default of " + Texts.getOverflowActionText(DEFAULT_OVERFLOW_ACTION).toLowerCase() + " (" + DEFAULT_OVERFLOW_ACTION + ").");
            overflowActionNode.setValue(DEFAULT_OVERFLOW_ACTION);
            overflowAction = DEFAULT_OVERFLOW_ACTION;
            configChanged = true;
        }

        //get the max stacks node from config
        CommentedConfigurationNode maxStacksNode = rootNode.getNode("maxStacks");

        maxStacks = loadConfigurationNode(maxStacksNode, Texts.CONFIG_MAX_STACKS_COMMENT, DEFAULT_MAX_STACKS).get();

        //check that the max stacks configured is a positive number, and reset it if not
        if(maxStacks < 1) {
            logger.warn("Max stacks was set to " + maxStacks + " but must be > 0. Resetting to default of " + DEFAULT_MAX_STACKS + ".");
            maxStacksNode.setValue(DEFAULT_MAX_STACKS);
            maxStacks = DEFAULT_MAX_STACKS;
            configChanged = true;
        }

        //save the config
        if(configChanged) {
            saveConfig(rootNode);
        }
    }

    //a type-agnostic node loading function, not complete yet
    /*
    private <T> Optional<T> loadConfigurationNode(CommentedConfigurationNode node, String comment, @Nullable T defaultValue) {
        T nodeValue = null;

        //if the node doesn't exist, or its comment is unset or incorrect, set the comment
        if(node.isVirtual() || !node.getComment().isPresent() || !node.getComment().get().equals(comment)) {
            node.setComment(comment);
            configChanged = true;
        }

        if(defaultValue != null) {
            //get the current value, or null if it's unset
            Object nodeValueObj = node.getValue();
            if(nodeValueObj.getClass().isInstance(defaultValue)) { //TODO: find out if this works
                nodeValue = (T) nodeValueObj;
            }
            else {
                logger.warn("A configuration value of type " +  + " was found when it should have been " +); //TODO: finish writing this
            }

            //if the value has never been set, set it silently
            if (node.isVirtual() || nodeValue == null) {
                node.setValue(defaultValue);
                nodeValue = defaultValue;
                configChanged = true;
            }
        }

        return Optional.ofNullable(nodeValue);
    }
    */

    //loads a configuration node's comment and value, making changes as necessary, and tracking whether the config needs to be saved
    //defaultValue can be null if the node only contains other nodes. in this case, null will be returned as well.
    //TODO: make this work with more value types than just ints (see the above commented function)
    private Optional<Integer> loadConfigurationNode(CommentedConfigurationNode node, String comment, @Nullable Integer defaultValue) {
        Integer nodeValue = null;

        //if the node doesn't exist, or its comment is unset or incorrect, set the comment
        if(node.isVirtual() || !node.getComment().isPresent() || !node.getComment().get().equals(comment)) {
            node.setComment(comment);
            configChanged = true;
        }

        if(defaultValue != null) {
            //get the current value, or -2 if it's unset
            nodeValue = node.getInt(-2);

            //if the value has never been set, set it silently
            if (node.isVirtual() || nodeValue == -2) {
                node.setValue(defaultValue);
                nodeValue = defaultValue;
                configChanged = true;
            }
        }

        return Optional.ofNullable(nodeValue);
    }

    protected void saveConfig(CommentedConfigurationNode rootNode) {
        try {
            configLoader.save(rootNode);
        } catch (IOException e) {
            logger.warn(Texts.SAVE_IO_ERROR, e);
        }
    }
}
