package com.pseudo.scripts.moneymaking.pohplanker.nodes;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;
import com.pseudo.scripts.moneymaking.pohplanker.fw.Node;
import com.pseudo.scripts.moneymaking.pohplanker.fw.logic.ButlerHandler;


public class HandleConversation extends Node {

    private final ButlerHandler butlerHandler;

    public HandleConversation(PvpPlanker pvpPlanker) {
        super(pvpPlanker);
        this.butlerHandler = new ButlerHandler(pvpPlanker);
    }

    @Override
    public boolean validate() {
        return butlerHandler.isInConvo();
    }

    @Override
    public void execute() {
        butlerHandler.handlePlankConversion();
    }

    @Override
    public String status() {
        return "Handling dialogues";
    }
}
