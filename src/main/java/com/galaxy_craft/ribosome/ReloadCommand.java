package com.galaxy_craft.ribosome;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class ReloadCommand implements CommandExecutor {
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .description(Text.of(Texts.COMMAND_RELOAD_DESCRIPTION))
                .permission("ribosome.reload")
                .executor(new ReloadCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Ribosome.getInstance().loadConfig(); //TODO: pass information and messages for non-successful loads
        src.sendMessage(Text.of(Texts.RELOAD_CONFIG_SUCCESS));
        return CommandResult.success();
    }
}