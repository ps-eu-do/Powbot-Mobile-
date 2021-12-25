package com.pseudo.scripts.moneymaking.pohplanker.nodes;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;
import com.pseudo.scripts.moneymaking.pohplanker.fw.Node;
import com.pseudo.scripts.moneymaking.pohplanker.fw.logic.ButlerHandler;
import com.pseudo.scripts.moneymaking.pohplanker.fw.logic.TeleportHandler;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Bank;

public class TeleportToHouse extends Node {

    private final PvpPlanker pvpPlanker;
    private final ButlerHandler butlerHandler;
    private final TeleportHandler teleportHandler;

    public TeleportToHouse(PvpPlanker pvpPlanker) {
        super(pvpPlanker);
        this.pvpPlanker = pvpPlanker;
        this.butlerHandler = new ButlerHandler(pvpPlanker);
        this.teleportHandler = new TeleportHandler(pvpPlanker);
    }

    @Override
    public boolean validate() {
        return butlerHandler.shouldTeleToHouse();
    }

    @Override
    public void execute() {

        if (Bank.opened()) {
            pvpPlanker.plankCount += butlerHandler.getLogsAmount();
            closeBank();
        }

        if (teleportHandler.teleportToHouse())
            Condition.wait(() -> !butlerHandler.isInHouse(), Random.nextInt(800, 1800), 1);

    }

    @Override
    public String status() {
        return "Teleporting to house";
    }

    private void closeBank() {
        if (Bank.close()) Condition.wait(() -> !Bank.opened(), Random.nextInt(600, 1200), 1);
    }
}
