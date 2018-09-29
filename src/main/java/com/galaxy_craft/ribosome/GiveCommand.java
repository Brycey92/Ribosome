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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.Optional;

import static java.lang.Math.min;

public class GiveCommand implements CommandExecutor {
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .description(Text.of(Texts.COMMAND_GIVE_DESCRIPTION))
                .permission("ribosome.item.others")
                .executor(new GiveCommand())
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                        GenericArguments.catalogedElement(Text.of("item"), ItemType.class),
                        GenericArguments.optional(GenericArguments.integer(Text.of("amount")))
                )
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return giveItem(src, args);
    }

    public static CommandResult giveItem(CommandSource src, CommandContext args) throws CommandException {
        Optional<Player> playerOptional = args.<Player>getOne("player");
        Player target;

        if(playerOptional.isPresent()) {
            target = playerOptional.get();
        } else {
            throw new CommandException(Text.of("Unable to parse player"));
        }

        Inventory inventory = target.getInventory(); //TODO: make a copy of this to add cancel-on-overflow functionality

        Optional<ItemType> itemTypeOptional = args.<ItemType>getOne("item");
        ItemType itemType;

        if(itemTypeOptional.isPresent()) {
            itemType = itemTypeOptional.get();
        } else {
            throw new CommandException(Text.of("Unable to parse item")); //TODO: lookup item by alias or ID
        }
        //create an ItemStack from the ItemType in the command argument
        ItemStack itemStack = itemType.getTemplate().createStack();

        int maxAmount = itemStack.getMaxStackQuantity();
        int amount = args.<Integer>getOne("amount").orElse(Ribosome.getDefaultAmount());
        int amountLeft = amount;
        int amountDropped = 0;
        int stacksDropped = 0;

        //give the requested items, one stack at a time
        int overflowAction = Ribosome.getOverflowAction();
        int maxStacks = Ribosome.getMaxStacks();
        boolean outOfSpace = false;
        while(amountLeft > 0 && ((!outOfSpace && overflowAction == 0) || (stacksDropped < maxStacks && overflowAction == 1))) {
            //we can only give up to the max stack size, and we don't want to give more than was requested
            int origStackSize = min(amountLeft, maxAmount);
            itemStack.setQuantity(origStackSize);
            int amountGivenThisIteration;

            if(overflowAction == 0 || !outOfSpace) {
                //offer() reduces the ItemStack's quantity by the number of items given
                inventory.offer(itemStack);
                amountGivenThisIteration = origStackSize - itemStack.getQuantity();
                amountLeft -= amountGivenThisIteration;

                //we weren't able to give all the items requested
                if(amountGivenThisIteration < origStackSize) {
                    outOfSpace = true;
                }
            }
            else if(overflowAction == 1) { //overflow action is not 0, and the inventory is out of space
                //spawn an itemstack at the target's location
                World world = target.getWorld();
                Entity items = world.createEntity(EntityTypes.ITEM, target.getPosition());
                items.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
                CauseStackManager.StackFrame stackFrame = Sponge.getCauseStackManager().pushCauseFrame();
                stackFrame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
                if(world.spawnEntity(items)) {
                    amountLeft -= origStackSize;
                    amountDropped += origStackSize;
                    stacksDropped++;
                } else {
                    //TODO: something went wrong here, so report it
                    src.sendMessage(Text.of("Something went wrong, but I don't know what. If you see this, tell Brycey92 that the plugin reached line 113 in GiveCommand, and now he needs to actually finish writing it."));
                    break;
                }
            }
        }

        //print relevant messages after giving is complete
        if(amountLeft == 0 && amountDropped == 0) {
            src.sendMessage(Text.of("Gave all " + amount + " requested items."));
        }
        else {
            Text text = Text.of("Inventory is full! Gave " + (amount - amountLeft - amountDropped) + " items out of " + amount + " requested.");
            if(amountDropped > 0) {
                text = text.concat(Text.of(" Dropped " + amountDropped + " additional."));
            }
            if(amountLeft > 0) {
                text = text.concat(Text.of(" Lost " + amountLeft + "."));
            }
            src.sendMessage(text);
        }

        //debug
        src.sendMessage(Text.of("Item name: " + itemType.getName()));

        return CommandResult.builder()
                .affectedItems(amount - amountLeft)
                .successCount(1)
                .build();
    }
}
