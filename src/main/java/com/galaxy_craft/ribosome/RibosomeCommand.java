package com.galaxy_craft.ribosome;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;

public class RibosomeCommand implements CommandExecutor {
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .description(Text.of(Texts.COMMAND_RIBOSOME_DESCRIPTION))
                .permission("ribosome.base")
                .executor(new RibosomeCommand())
                .child(ReloadCommand.getCommandSpec(), "reload")
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessage(Text.of("Ribosome commands:"));
        src.sendMessage(Text.of("/item <item> [<amount>]"));
        src.sendMessage(Text.of("  - " + Texts.COMMAND_ITEM_DESCRIPTION));
        src.sendMessage(Text.of("  - Aliases: i"));
        src.sendMessage(Text.of("/giveitem <player> <item> [<amount>]"));
        src.sendMessage(Text.of("  - " + Texts.COMMAND_GIVE_DESCRIPTION));
        src.sendMessage(Text.of("  - Aliases: gi"));
        src.sendMessage(Text.of("/ribosome reload"));
        src.sendMessage(Text.of("  - " + Texts.COMMAND_RELOAD_DESCRIPTION));
        return CommandResult.empty();
    }
}