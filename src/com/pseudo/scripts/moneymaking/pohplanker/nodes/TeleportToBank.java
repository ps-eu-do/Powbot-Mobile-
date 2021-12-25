package com.pseudo.scripts.moneymaking.pohplanker.nodes;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;
import com.pseudo.scripts.moneymaking.pohplanker.fw.Node;
import com.pseudo.scripts.moneymaking.pohplanker.fw.logic.ButlerHandler;
import com.pseudo.scripts.moneymaking.pohplanker.fw.logic.TeleportHandler;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Bank;

public class TeleportToBank extends Node {

    private final PvpPlanker pvpPlanker;
    private final ButlerHandler butlerHandler;
    private final TeleportHandler teleportHandler;

    public TeleportToBank(PvpPlanker pvpPlanker) {
        super(pvpPlanker);
        this.pvpPlanker = pvpPlanker;
        this.butlerHandler = new ButlerHandler(pvpPlanker);
        this.teleportHandler = new TeleportHandler(pvpPlanker);
    }

    @Override
    public boolean validate() {
        return butlerHandler.shouldTeleToBank();
    }

    @Override
    public void execute() {

        if (teleportHandler.teleportToBank())
            Condition.wait(butlerHandler::isInHouse, Random.nextInt(900, 1500), 1);

    }

    @Override
    public String status() {
        return "Teleporting to bank";
    }

}
