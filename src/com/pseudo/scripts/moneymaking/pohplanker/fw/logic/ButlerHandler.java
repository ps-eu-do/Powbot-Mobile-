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
        GameObject portal = Objects.stream().within(30).name("Portal").first();
        return portal.valid();
    }

    public boolean shouldTeleToBank() {
        return isInHouse() && !Inventory.stream().filter(i -> i.name().contains("ogs")).first().valid();
    }

    public boolean shouldTeleToHouse() {
        return !isInHouse() && Inventory.stream().filter(i -> i.name().contains("ogs")).first().valid();
    }

    public boolean isInConvo() { //Verifies we meet all the criteria for plank conversion whilst talking to our butler
        return isInHouse() && Inventory.stream().filter(i -> i.name().contains("ogs")).first().valid() && chatIsOpen();
    }

    public boolean shouldStartConvo() { //Verifies we meet all the criteria for planking whilst NOT conversing with butler
        return isInHouse() && Inventory.stream().filter(i -> i.name().contains("ogs")).first().valid() && !chatIsOpen();
    }

    public boolean chatIsOpen() {
        return Chat.chatting() || Chat.pendingInput();
    }

    public boolean callButler() {
        Component callInterface = Components.stream().action("Call Servant").first();
        return canCallButler() && callInterface.interact("Call Servant");
    }

    public boolean canCallButler() {
        Component callInterface = Components.stream().action("Call Servant").first();
        return callInterface.valid() && callInterface.visible();
    }

    public int getLogsAmount() {
        int amt = 0;
        for (Item i : Inventory.stream()) {
            if (i.valid() && i.name().contains("ogs")) {
                amt++;
            }
        }
        return amt;
    }

    public boolean handlePlankConversion() {

        ChatOptionStream dialogs = Chat.stream();

        Npc butler = Npcs.stream().filter(i -> i.name().contains("utler")).nearest().first();

        if (!Chat.chatting() && !Chat.pendingInput()) return false;

        if (Chat.canContinue()) {
            System.out.println("Clicking continue");
            if (Chat.clickContinue()) return handlePlankConversion();
        }

        if (Chat.pendingInput()) {
            System.out.println("Inputting value");
            Chat.sendInput("" + getLogsAmount());
            return Condition.wait(this::chatIsOpen, Random.nextInt(600, 1200), 1);
        }

        Component ohDearNoGold = Components.stream().textContains("Oh dear,").first();
        if (ohDearNoGold.valid() && ohDearNoGold.visible()) {
            System.out.println("Out of gold, stopping.");
        }
        for (ChatOption d : dialogs) {
            for (String a : ourActions) {
                if (d.valid()) {
                    System.out.println("Chat option valid");
                    if (d.text().contains("Go to the sawmill") || d.text().contains("Take to bank")
                            || (d.text().contains("Take") && !d.text().contains(pvpPlanker.logName))) {
                        System.out.println("Incorrect log configuration - using logs on butler");
                        if (useItemOnNpc("ogs", butler)) {
                            Condition.wait(this::chatIsOpen, Random.nextInt(800, 1200), 1);
                            return handlePlankConversion();
                        }
                    }
                    if (!Chat.canContinue()) {
                        System.out.println("No continue option");
                        if (d.text().contains(a)) {
                            System.out.println("Selecting option: " + a);
                            if (d.select()) {
                                Condition.wait(() -> !d.valid(), Random.nextInt(200, 400), 1);
                                return handlePlankConversion();
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean useItemOnNpc(String itemNameContains, Npc npc) {
        Item item = Inventory.stream().filter(i -> i.name().contains(itemNameContains)).first();
        if (!item.valid() || !npc.valid()) return false;
        System.out.println("Interacting with butler");
        if (Game.tab() != Game.Tab.INVENTORY) Game.tab(Game.Tab.INVENTORY);
        if (!npc.inViewport()) Movement.walkTo(npc);
        if (Inventory.selectedItemIndex() == -1) {
            if (item.click())
                Condition.wait(() -> Inventory.selectedItemIndex() != -1, 1000, 1);
        }
        return Inventory.selectedItemIndex() != -1 && npc.interact(npc.actions().get(0));
    }

}
