package com.pseudo.scripts.moneymaking.pohplanker.fw.logic;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;
import org.powbot.api.Random;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;

public class ItemHandler {

    private final PvpPlanker pvpPlanker;

    public ItemHandler(PvpPlanker pvpPlanker) {
        this.pvpPlanker = pvpPlanker;
    }

    public Item coins, laws;

    public boolean hasEnoughLaws() {
        return laws.valid() && laws.stackSize() >= 2;
    }

    public boolean hasEnoughCoins() {
        if (getPlank() == null) return false;
        return coins.valid() && coins.stackSize() >= (10000 + (getPlank().costToMake * 26));
    }

    public boolean withdrawLaws() {
        return Bank.withdraw("Law rune", Random.nextInt(50, 150));
    }

    public boolean withdrawCoins(int amountToWithdraw) {
        return Bank.withdraw("Coins", amountToWithdraw);
    }

    public int getCoinsToWithdraw() {
        return Random.nextInt(getMinimumCoinAmount() * 3, getMinimumCoinAmount() * 6);
    }

    public int getMinimumCoinAmount() {
        if (getPlank() == null) return 100000;
        return getPlank().costToMake * 26 + 10000;
    }

    private Plank getPlank() {
        for (Plank plank : Plank.values()) {
            if (plank.getInputName().equals(pvpPlanker.logName)) {
                return plank;
            }
        }
        return null;
    }

    public enum Plank {
        PLANK("Logs", "Plank", 100),
        OAK_PLANK("Oak logs", "Oak plank", 2500),
        TEAK_PLANK("Teak logs", "Teak plank", 500),
        MAHOGANY_PLANK("Mahogany logs", "Mahogany plank", 1500);

        final String inputName;
        final String outputName;
        final int costToMake;

        Plank(String inputName, String outputName, int costToMake) {
            this.inputName = inputName;
            this.outputName = outputName;
            this.costToMake = costToMake;
        }

        public String getInputName() {
            return inputName;
        }

        public int getCostToMake() {
            return costToMake;
        }

        public String getOutputName() {
            return outputName;
        }

        @Override
        public String toString() {
            return "Plank{" +
                    "inputName='" + inputName + '\'' +
                    ", outputName='" + outputName + '\'' +
                    ", costToMake=" + costToMake +
                    '}';
        }
    }

}
