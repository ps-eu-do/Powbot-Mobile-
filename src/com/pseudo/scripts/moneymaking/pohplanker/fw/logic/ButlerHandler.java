package com.pseudo.scripts.moneymaking.pohplanker.fw.logic;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.widget.ChatOptionStream;

public class ButlerHandler {

    private final PvpPlanker pvpPlanker;

    public ButlerHandler(PvpPlanker pvpPlanker) {
        this.pvpPlanker = pvpPlanker;
    }

    private final String[] ourActions = {"Take to sawmill:", "Yes", "Okay, ", "Take them back", "Sawmill", "Oh dear,"};

    public boolean isInHouse() {
        GameObject portal = Objects.stream().within(30).name("Portal").action("Remove board advert").first();
        return portal.valid();
    }

    public boolean shouldTeleToBank() {
        return isInHouse() && Inventory.stream().filtered(i -> i.name().toLowerCase().contains("logs")).isEmpty();
    }

    public boolean shouldTeleToHouse() {
        return !isInHouse() && !Inventory.stream().filtered(i -> i.name().toLowerCase().contains("logs")).isEmpty();
    }

    public boolean isInConvo() { //Verifies we meet all the criteria for plank conversion whilst talking to our butler
        return isInHouse() && !Inventory.stream().filtered(i -> i.name().toLowerCase().contains("logs")).isEmpty() && chatIsOpen();
    }

    public boolean shouldStartConvo() { //Verifies we meet all the criteria for planking whilst NOT conversing with butler
        return isInHouse() && !Inventory.stream().filtered(i -> i.name().toLowerCase().contains("logs")).isEmpty() && !chatIsOpen();
    }

    public boolean chatIsOpen() {
        return Chat.chatting() || Chat.pendingInput();
    }

    public boolean callButler() {
        Component callInterface = Components.stream(370).text("Call Servant").first();
        return canCallButler() && callInterface.interact("Call Servant");
    }

    public boolean canCallButler() {
        Component callInterface = Components.stream(370).text("Call Servant").first();
        return callInterface.valid() && callInterface.visible();
    }

    public int getLogsAmount() {
        return (int) Inventory.stream().filtered(i -> i.name().toLowerCase().contains("logs")).count();
    }

    public boolean handlePlankConversion() {

        ChatOptionStream dialogs = Chat.stream();

        Npc butler = Npcs.stream().filtered(i -> i.name().toLowerCase().contains("butler")).first();

        if (!Chat.chatting() && !Chat.pendingInput()) return false;

        if (Chat.canContinue()) {
            System.out.println("Clicking continue");
            if (Chat.clickContinue()) return handlePlankConversion();
        }

        if (Chat.pendingInput()) {
            System.out.println("Inputting value");
            Chat.sendInput("" + getLogsAmount());
            return Condition.wait(this::chatIsOpen, Random.nextInt(300, 600), 5);
        }

        Component ohDearNoGold = Components.stream().textContains("Oh dear,").first();
        if (ohDearNoGold.valid() && ohDearNoGold.visible()) {
            System.out.println("Out of gold, stopping.");
            pvpPlanker.controller.stop();
        }
        for (ChatOption d : dialogs) {
            if (d.valid()) {
                System.out.println("Chat option valid");
                if (d.text().contains("Go to the sawmill") || d.text().contains("Take to bank")
                        || (d.text().contains("Take") && !d.text().contains(pvpPlanker.logName))) {
                    System.out.println("Incorrect log configuration - using logs on butler");
                    if (useItemOnNpc("ogs", butler)) {
                        Condition.wait(this::chatIsOpen, Random.nextInt(600, 900), 10);
                        return handlePlankConversion();
                    }
                }
                if (!Chat.canContinue()) {
                    System.out.println("No continue option");
                    if (Chat.completeChat(ourActions)) {
                       //Condition.wait(() -> !d.valid(), Random.nextInt(200, 400), 2);
                        return handlePlankConversion();
                    }
                }
            }
        }
        return true;
    }

    public boolean useItemOnNpc(String itemNameContains, Npc npc) {
        Item item = Inventory.stream().filtered(i -> i.name().contains(itemNameContains)).first();
        if (!item.valid() || !npc.valid()) return false;
        if (!npc.inViewport()) Camera.turnTo(npc);
        if (Inventory.selectedItemIndex() == -1) {
            if (Game.tab(Game.Tab.INVENTORY)) {
                if (item.click()) {
                    Condition.wait(() -> Inventory.selectedItemIndex() != -1, 400, 5);
                }
            }
        }
        return Inventory.selectedItemIndex() != -1 && npc.interact(npc.actions().get(0));
    }

}
