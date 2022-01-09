package com.pseudo.scripts.moneymaking.pohplanker;

import com.pseudo.scripts.moneymaking.pohplanker.fw.Node;
import com.pseudo.scripts.moneymaking.pohplanker.nodes.*;
import org.powbot.api.script.*;
import org.powbot.api.script.paint.Paint;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.service.ScriptUploader;

import java.util.ArrayList;
import java.util.List;

@ScriptManifest(name = "Λ PvP Planker",
        description = "Crafts planks using your butler and banking at the appropriate bank chest on the PvP worlds",
        version = "v0.1",
        category = ScriptCategory.MoneyMaking)

@ScriptConfiguration(
        enabled = true,
        visible = true,
        name = "Input log",
        description = "The name of the logs to convert",
        optionType = OptionType.STRING,
        defaultValue = "Oak logs",
        allowedValues = {"Logs", "Oak logs", "Teak logs", "Mahogany logs"}
)

public class PvpPlanker extends AbstractScript {

    private final List<Node> nodes = new ArrayList<>();

    public int plankCount = 0;

    private String status = "";

    public String logName = "";

    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Λ PvP Planker", "", "bbbf854", true, false);
    }


    @Override
    public void poll() {
        for (final Node node : nodes) {
            if (node.validate()) {
                status = node.status();
                node.execute();
                break;
            }
        }
    }

    @Override
    public void onStart() {
        nodes.add(new HandleConversation(this));
        nodes.add(new CallButler(this));
        nodes.add(new Banking(this));
        nodes.add(new TeleportToBank(this));
        nodes.add(new TeleportToHouse(this));
        final Paint paint = PaintBuilder.newBuilder().addString("Status: ", () -> status).addString("Planks made: ", () -> "" + plankCount + " (" + getPerHour(plankCount, controller.getRuntime(true)) + ")").build();
        addPaint(paint);
        System.out.println("" + getOption("Input log"));
        logName = getOption("Input log");
    }

    private long getPerHour(long input, long elapsed) {
        return (int) ((input) * 3600000D / elapsed);
    }


}
