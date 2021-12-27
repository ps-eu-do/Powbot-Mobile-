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
        return !butlerHandler.chatIsOpen() && butlerHandler.shouldStartConvo() && !butlerHandler.isInConvo();
    }

    @Override
    public void execute() {

        //Fail safe start
        GameObject portal = Objects.stream().within(15).name("Portal").action("Build mode").first();
        if (portal.valid()) {
            for (String s : portal.actions()) {
                if (s != null && s.contains("Home")) {
                    if (portal.interact("Home")) Condition.wait(butlerHandler::isInHouse, 400, 5);
                }
            }
        }
        //End
        if (butlerHandler.canCallButler()) {
            if (butlerHandler.callButler()) {
                Condition.wait(Chat::chatting, 400, 6);
            }
            Npc butler = Npcs.stream().filtered(i -> i.name().toLowerCase().contains("butler")).first();
            if (!butlerHandler.isInConvo() && butler.valid() && butler.tile().distanceTo(Players.local()) <= 3) {
                if (butler.interact("Talk-to")) {
                    Condition.wait(butlerHandler::isInConvo, 600, 10);
                }
            }
        } else {
            if (Game.tab(Game.Tab.SETTINGS)) {
                Component houseOptions = Components.stream(116).action("View House Options").first();
                if (houseOptions.valid() && houseOptions.visible()) {
                    if (houseOptions.click()) {
                        Condition.wait(butlerHandler::canCallButler, Random.nextInt(300, 600), 10);
                    }
                } else {
                    Component cogOption = Components.stream(116).action("Controls").first();
                    if (cogOption.valid()) {
                        if (cogOption.click()) {
                            Condition.wait(houseOptions::valid, 200, 11);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String status() {
        return "Calling butler";
    }
}
