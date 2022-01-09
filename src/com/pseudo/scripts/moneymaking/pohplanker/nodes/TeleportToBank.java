package com.pseudo.scripts.moneymaking.pohplanker.nodes;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;
import com.pseudo.scripts.moneymaking.pohplanker.fw.Node;
import com.pseudo.scripts.moneymaking.pohplanker.fw.logic.ButlerHandler;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.Magic;

public class TeleportToBank extends Node {

    private final PvpPlanker pvpPlanker;
    private final ButlerHandler butlerHandler;

    public TeleportToBank(PvpPlanker pvpPlanker) {
        super(pvpPlanker);
        this.pvpPlanker = pvpPlanker;
        this.butlerHandler = new ButlerHandler(pvpPlanker);
    }

    @Override
    public boolean validate() {
        return butlerHandler.shouldTeleToBank();
    }

    @Override
    public void execute() {

        if (Game.tab(Game.Tab.MAGIC)) {
            if (Magic.Spell.LUMBRIDGE_TELEPORT.canCast() && Magic.Spell.LUMBRIDGE_TELEPORT.cast()) {
                Condition.wait(() -> !butlerHandler.isInHouse(), Random.nextInt(300, 600), 10);
            } else {
                System.out.println("Can't teleport - terminating for safety");
                pvpPlanker.controller.stop();
            }
        }

    }

    @Override
    public String status() {
        return "Teleporting to bank";
    }

}
