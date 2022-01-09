package com.pseudo.scripts.moneymaking.pohplanker.nodes;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;
import com.pseudo.scripts.moneymaking.pohplanker.fw.Node;
import com.pseudo.scripts.moneymaking.pohplanker.fw.logic.ButlerHandler;
import com.pseudo.scripts.moneymaking.pohplanker.fw.logic.ItemHandler;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;


public class Banking extends Node {

    private final ButlerHandler butlerHandler;
    private final PvpPlanker pvpPlanker;
    private final ItemHandler itemHandler;

    public Banking(PvpPlanker pvpPlanker) {
        super(pvpPlanker);
        this.pvpPlanker = pvpPlanker;
        this.itemHandler = new ItemHandler(pvpPlanker);
        butlerHandler = new ButlerHandler(pvpPlanker);
    }

    @Override
    public boolean validate() {
        return Inventory.stream().name(pvpPlanker.logName).isEmpty() && !butlerHandler.isInHouse();
    }

    @Override
    public void execute() {
        if (Bank.opened()) {
            Item plank = Inventory.stream().filtered(i -> i.name().toLowerCase().contains("plank")).first();
            if (plank.valid()) {
                if (Bank.deposit(plank.name(), 0)) {
                    Condition.wait(() -> !plank.valid(), 200, 20);
                }
            } else {

                itemHandler.coins = Inventory.stream().name("Coins").first();
                itemHandler.laws = Inventory.stream().name("Law rune").first();

                if (!itemHandler.hasEnoughCoins()) {
                    withdrawCoins();
                    return;
                }

                if (!itemHandler.hasEnoughLaws()) {
                    withdrawLaws();
                    return;
                }

                if (!Bank.stream().name(pvpPlanker.logName).first().valid()) {
                    pvpPlanker.controller.stop();
                } else {
                    if (Bank.withdraw(pvpPlanker.logName, 0)) {
                        Condition.wait(() -> !Inventory.stream().name(pvpPlanker.logName).isEmpty(), Random.nextInt(200, 400), 20);
                    }
                }
            }
        } else {
            GameObject bankChest = Objects.stream().within(15).name("Bank chest").nearest().first();
            if (bankChest.valid()) {
                if (bankChest.inViewport()) {
                    if (bankChest.interact("Use")) {
                        Condition.wait(Bank::opened, Random.nextInt(200, 600), 20);
                    }
                } else {
                    Camera.turnTo(bankChest);
                }
            }
        }
    }

    private boolean withdrawCoins() {
        int coinsToWithdraw = itemHandler.getCoinsToWithdraw();
        Item coins = Bank.stream().name("Coins").first();
        if (coins.valid() && coins.stackSize() >= coinsToWithdraw) {
            if (itemHandler.withdrawCoins(coinsToWithdraw)) {
                return Condition.wait(itemHandler::hasEnoughCoins, 300, 6);
            }
        } else {
            pvpPlanker.controller.stop();
        }
        return false;
    }

    private boolean withdrawLaws() {
        Item lawRune = Bank.stream().name("Law rune").first();
        if (lawRune.valid() && lawRune.stackSize() >= 2) {
            if (itemHandler.withdrawLaws()) {
                return Condition.wait(itemHandler::hasEnoughLaws, 300, 6);
            }
        } else {
            pvpPlanker.controller.stop();
        }
        return false;
    }

    @Override
    public String status() {
        return "Banking";
    }
}
