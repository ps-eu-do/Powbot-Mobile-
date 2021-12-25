package com.pseudo.scripts.moneymaking.pohplanker.fw;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;

public abstract class Node {

    private PvpPlanker pvpPlanker;

    public Node(final PvpPlanker pvpPlanker) {
        this.pvpPlanker = pvpPlanker;
    }

    public abstract boolean validate();

    public abstract void execute();

    public abstract String status();

}
