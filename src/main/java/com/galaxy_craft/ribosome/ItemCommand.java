package com.galaxy_craft.ribosome;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;


import java.lang.reflect.Type;
import java.util.Optional;

import static java.lang.Math.min;

public class ItemCommand implements CommandExecutor {
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .description(Text.of(Texts.COMMAND_ITEM_DESCRIPTION))
                .permission("ribosome.item.base")
                .executor(new ItemCommand())
                .arguments(
                        GenericArguments.catalogedElement(Text.of("item"), ItemType.class),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")))
                )
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            args.putArg("player", (Player) src);

            return GiveCommand.giveItem(src, args);
        }
        else {
            throw new CommandException(Text.of("The /item command must be run as a player. Please use /giveitem instead."));
        }
    }
}