package com.pseudo.scripts.moneymaking.pohplanker.fw.logic;

import com.pseudo.scripts.moneymaking.pohplanker.PvpPlanker;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.Magic;

public class TeleportHandler {

    public TeleportHandler(PvpPlanker pvpPlanker) {
    }

    private boolean teleport(Magic.MagicSpell magicSpell) {
        return magicSpell.cast();
    }

    public boolean teleportToHouse() {
        if (Game.tab() != Game.Tab.MAGIC) {
            Game.tab(Game.Tab.MAGIC);
        }
        return teleport(Magic.Spell.TELEPORT_TO_HOUSE);
    }

    public boolean teleportToBank() {
        if (Game.tab() != Game.Tab.MAGIC) {
            Game.tab(Game.Tab.MAGIC);
        }
        return /*Skill.Magic.realLevel() >= 45 ? teleport(Magic.Spell.CAMELOT_TELEPORT) : */ teleport(Magic.Spell.LUMBRIDGE_TELEPORT);
    }

}
