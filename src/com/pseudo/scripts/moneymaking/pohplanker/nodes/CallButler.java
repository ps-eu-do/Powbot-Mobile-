package com.pseudo.scripts.moneymaking.pohplanker.nodes;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;
import com.pseudo.scripts.moneymaking.pohplanker.fw.Node;
import com.pseudo.scripts.moneymaking.pohplanker.fw.logic.ButlerHandler;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;

public class CallButler extends Node {

    private final ButlerHandler butlerHandler;

    public CallButler(PvpPlanker pvpPlanker) {
        super(pvpPlanker);
        this.butlerHandler = new ButlerHandler(pvpPlanker);
    }

    @Override
    public boolean validate() {
        System.out.println("Should chat: " + butlerHandler.shouldStartConvo());
        return !butlerHandler.chatIsOpen() && butlerHandler.shouldStartConvo() && !butlerHandler.isInConvo();
    }

    @Override
    public void execute() {

        //Fail safe start
        GameObject portal = Objects.stream().filter(i -> i.name().equals("Portal") && i.actions().size() > 3).nearest().first();
        if (portal != null) {
            for (String s : portal.actions()) {
                if (s != null && s.contains("Home")) {
                    if (portal.interact("Home")) Condition.wait(butlerHandler::isInHouse, 1200, 1);
                }
            }
        }
        //End
        if (butlerHandler.canCallButler()) {
            if (butlerHandler.callButler()) {
                Condition.wait(Chat::chatting, 2200, 1);
            }
            Npc butler = Npcs.stream().filter(n -> n.name().contains("utler")).nearest().first();
            if (!butlerHandler.isInConvo() && butler.valid() && butler.tile().distanceTo(Players.local()) <= 3) {
                if (butler.interact("Talk-to")) {
                    Condition.wait(butlerHandler::isInConvo, 1200, 1);
                }
            }
        } else {
            if (Game.tab() == Game.Tab.SETTINGS) {
                Component houseOptions = Components.stream().action("View House Options").first();
                if (houseOptions.valid()) {
                    if (houseOptions.click()) {
                        Condition.wait(butlerHandler::canCallButler, Random.nextInt(300, 600), 1);
                    }
                } else {
                    Component cogOption = Components.stream().id(116, 110).first();
                    if (cogOption.valid()) {
                        if (cogOption.click()) {
                            Condition.wait(houseOptions::valid, 1000, 1);
                        }
                    }
                }
            } else {
                if (Game.tab(Game.Tab.OPTIONS)) {
                    Condition.wait(() -> Game.tab() == Game.Tab.OPTIONS, Random.nextInt(400, 600), 1);
                }
            }
        }
    }

    @Override
    public String status() {
        return "Calling butler";
    }
}
