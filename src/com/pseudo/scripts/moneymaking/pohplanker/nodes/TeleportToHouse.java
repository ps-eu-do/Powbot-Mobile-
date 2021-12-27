package com.pseudo.scripts.moneymaking.pohplanker.nodes;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;
import com.pseudo.scripts.moneymaking.pohplanker.fw.Node;
import com.pseudo.scripts.moneymaking.pohplanker.fw.logic.ButlerHandler;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.Magic;

public class TeleportToHouse extends Node {

    private final PvpPlanker pvpPlanker;
    private final ButlerHandler butlerHandler;

    public TeleportToHouse(PvpPlanker pvpPlanker) {
        super(pvpPlanker);
        this.pvpPlanker = pvpPlanker;
        this.butlerHandler = new ButlerHandler(pvpPlanker);
    }

    @Override
    public boolean validate() {
        return butlerHandler.shouldTeleToHouse();
    }

    @Override
    public void execute() {

        if (Bank.opened()) {
            pvpPlanker.plankCount += butlerHandler.getLogsAmount();
            if (Bank.close()) {
                Condition.wait(() -> !Bank.opened(), Random.nextInt(300, 600), 6);
            }
        }

        if (Game.tab(Game.Tab.MAGIC) && Magic.Spell.TELEPORT_TO_HOUSE.cast()) {
            Condition.wait(butlerHandler::isInHouse, Random.nextInt(300, 600), 10);
        }

    }

    @Override
    public String status() {
        return "Teleporting to house";
    }
}
