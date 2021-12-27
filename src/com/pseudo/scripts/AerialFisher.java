package com.pseudo.scripts;

import com.google.common.eventbus.Subscribe;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.event.MessageEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.item.InventoryItemStream;
import org.powbot.api.rt4.stream.item.ItemStream;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.AbstractScript;
import org.powbot.api.script.ScriptCategory;
import org.powbot.api.script.ScriptManifest;
import org.powbot.api.script.paint.Paint;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.service.ScriptUploader;

@ScriptManifest(name = "Λ Aerial Fisher", description = "Does the Aerial Fishing activity", version = "0.1", category = ScriptCategory.Hunter)
public class AerialFisher extends AbstractScript {

    private final String[] junkFish = {"Bluegill", "Common tench", "Mottled eel", "Greater siren"};

    private boolean hasBird = true;

    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Aerial Fisher", "", "bbbf854", true, true);
    }

    @Override
    public void onStart() {
        final Paint paint = PaintBuilder.newBuilder().x(1090).y(45).trackSkill(Skill.Fishing).trackSkill(Skill.Hunter).trackSkill(Skill.Cooking).build();
        addPaint(paint);
        super.onStart();
    }

    @Override
    public void poll() {

        ItemStream<InventoryItemStream> inventoryItems = Inventory.stream();

        if (inventoryItems.count() <= 27) {
            if (hasBait()) {
                Npc fishingSpot = Npcs.stream().within(10).name("Fishing spot").nearest().first();
                if (!fishingSpot.valid()) return;
                if (!hasBird) return;
                if (fishingSpot.inViewport()) {
                    if (fishingSpot.interact("Catch")) {
                        Condition.wait(() -> !hasBird, Random.nextInt(200, 4400), Random.nextInt(4, 8));
                        return;
                    }
                } else {
                    Movement.walkTo(fishingSpot);
                }
            } else {

                Item fish = inventoryItems.name(junkFish).first();

                if (fish.valid()) {
                    cutFish(junkFish);
                } else if (lootWorm()) {
                    Condition.wait(() -> Inventory.stream().filter(i -> i.name().equals("King worm")).first() != null, 300, 15);
                    return;
                }
            }
        } else {
            if (cutFish(junkFish)) Condition.wait(() -> hasBird, Random.nextInt(200, 400), Random.nextInt(4, 8));
            return;
        }
        if (!hasBird) cutFish(junkFish);
    }

    private boolean lootWorm() {
        GroundItem kingWormGroundItem = GroundItems.stream().within(10).name("King worm").nearest().first();
        if (!kingWormGroundItem.valid()) return false;
        return kingWormGroundItem.interact("Take");
    }

    private boolean cutFish(String[] fishNames) {
        Item fish = Inventory.stream().name(fishNames).first();
        Item knife = Inventory.stream().name("Knife").first();
        if (!fish.valid() || !knife.valid()) return false;
        if (!Inventory.opened()) return Inventory.open();
        if (knife.click()) {
            Condition.wait(() -> Inventory.selectedItemIndex() != -1, Random.nextInt(200, 400), Random.nextInt(3, 5));
            return fish.click();
        }
        return false;
    }

    @Subscribe
    public void onMessage(MessageEvent e) {
        String text = e.getMessage();
        if (text.contains("returns with")) {
            hasBird = true;
        } else if (text.contains("You send your")) {
            hasBird = false;
        }
    }

    private boolean hasBait() {
        Item worm = Inventory.stream().name("King worm").first();
        Item chunks = Inventory.stream().name("Fish chunks").first();
        return worm.valid() || chunks.valid();
    }

}