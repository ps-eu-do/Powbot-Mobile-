package com.pseudo.scripts.moneymaking;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.api.script.AbstractScript;
import org.powbot.api.script.ScriptCategory;
import org.powbot.api.script.ScriptManifest;
import org.powbot.api.script.paint.Paint;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.service.ScriptUploader;

import java.util.concurrent.Callable;

@ScriptManifest(name = "Swamp Toads", author = "Pseudo", version = "v0.1", description = "Collects and banks swamp toads at the gnome stronghold", category = ScriptCategory.MoneyMaking)
public class SwampToads extends AbstractScript {

    private final Tile bottomOfStaircase = new Tile(2444, 3434, 0);
    private final Tile entranceOfSwamp = new Tile(2420, 3508, 0);

    private Tile targetToadTile = null;
    private State state = null;
    private Player local;

    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Swamp Toads", "", "bbbf854", true, true);
    }


    @Override
    public void onStart() {
        final Paint paint = PaintBuilder.newBuilder().addString("Status: ", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return state.toString();
                    }
                }).trackInventoryItem(2150, "Swamp toad")
                .build();
        addPaint(paint);
        super.onStart();
    }

    @Override
    public void poll() {

        local = Players.local();

        state = getState();

        switch (state) {
            case CLIMB_DOWN_STAIRS:
                if (!Bank.opened()) {
                    if (climbStairs(false)) {
                        int plane = local.tile().getFloor();
                        Condition.wait(() -> Players.local().tile().getFloor() != plane, Random.nextInt(300, 600), 10);
                    }
                } else {
                    if (Bank.close()) {
                        Condition.wait(() -> !Bank.opened(), 3000, 1);
                    }
                }
                break;
            case CLIMB_UP_STAIRS:
                if (climbStairs(true)) {
                    int plane = local.tile().getFloor();
                    Condition.wait(() -> Players.local().tile().getFloor() != plane, Random.nextInt(300, 600), 10);
                }
                break;
            case WALK_TO_STAIRS:
                Movement.builder(bottomOfStaircase).setRunMin(30).setRunMax(100).move();
                break;
            case WALK_TO_SWAMP:
                Movement.builder(entranceOfSwamp).setRunMin(30).setRunMax(100).move();
                break;
            case COLLECT:
                if (collectToads()) {
                    Condition.wait(() -> Players.local().tile().equals(targetToadTile), Random.nextInt(300, 600), 10);
                }
                break;
            case BANK:
                doBanking();
                break;
        }

    }

    private enum State {
        CLIMB_UP_STAIRS("Climb up stairs"), CLIMB_DOWN_STAIRS("Climb down stairs"),
        WALK_TO_STAIRS("Walk to bank"), WALK_TO_SWAMP("Walk to swamp"),
        COLLECT("Collect toads"), BANK("Banking");

        private final String name;

        State(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private State getState() {
        if (Inventory.isFull()) {
            if (local.tile().getFloor() > 0) return State.BANK;
            else if (local.tile().distanceTo(bottomOfStaircase) < 10) {
                return State.CLIMB_UP_STAIRS;
            } else return State.WALK_TO_STAIRS;
        } else {
            if (local.tile().getFloor() > 0) {
                return State.CLIMB_DOWN_STAIRS;
            } else if (local.tile().distanceTo(entranceOfSwamp) < 15) {
                return State.COLLECT;
            } else return State.WALK_TO_SWAMP;
        }
    }

    private boolean climbStairs(boolean up) {
        GameObject stairs = Objects.stream().within(10).name("Staircase").nearest().first();
        if (!stairs.valid() || !local.valid()) return false;
        int plane = local.tile().getFloor();

        return (up ? stairs.interact("Climb-up") : stairs.interact("Climb-down"));
    }

    private boolean collectToads() {
        GroundItem swampToad = GroundItems.stream().within(15).name("Swamp toad").nearest().first();
        if (!swampToad.valid() || !local.valid()) return false;
        targetToadTile = swampToad.getTile();
        if (swampToad.inViewport()) {
            return swampToad.interact("Take");
        } else {
            Camera.turnTo(swampToad);
        }
        return false;
    }

    private boolean doBanking() {
        GameObject bankBooth = Objects.stream().within(15).name("Bank booth").nearest().first();
        if (!bankBooth.valid()) return false;
        if (Bank.opened()) {
            if (!Inventory.isEmpty()) {
                if (Bank.depositInventory()) {
                    Condition.wait(Inventory::isEmpty, Random.nextInt(300, 600), 10);
                }
            } else return Bank.close();
        } else return Bank.open();
        return false;
    }


}
