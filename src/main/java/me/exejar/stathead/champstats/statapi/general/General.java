package me.exejar.stathead.champstats.statapi.general;

import com.google.gson.JsonObject;
import me.exejar.stathead.champstats.statapi.HPlayer;
import me.exejar.stathead.champstats.statapi.HypixelGames;
import me.exejar.stathead.champstats.statapi.exception.ApiRequestException;
import me.exejar.stathead.champstats.statapi.exception.InvalidKeyException;
import me.exejar.stathead.champstats.statapi.exception.PlayerNullException;
import me.exejar.stathead.champstats.statapi.stats.Stat;
import me.exejar.stathead.champstats.statapi.stats.StatLong;
import me.exejar.stathead.champstats.statapi.stats.StatString;
import me.exejar.stathead.utils.ChatColor;
import me.exejar.stathead.utils.ChatUtils;
import me.exejar.stathead.utils.References;

import java.util.ArrayList;
import java.util.List;

public class General extends GeneralUtils {

    private JsonObject playersJson;
    private List<Stat> statList, formattedStatList;
    public Stat exp, quests, achpoints;

    public General(String playerName, String playerUUID) {
        super(playerName, playerUUID);
        statList = new ArrayList<>();
        if (setData(HypixelGames.GENERAL)) {
            statList = setStats(
                    exp = new StatLong("EXP", "networkExp", playersJson),
//                    quests = new StatInt("Quest", "", playersJson),
                    achpoints = new StatLong("AP", "achievementPoints", playersJson)
            );
        } else {
            formattedStatList.add(new StatString("Level", ChatColor.RED + "NICKED"));
        }
    }

    public General(String playerName, String playerUUID, HPlayer hPlayer) {
        super(playerName, playerUUID);
    }

    @Override
    public boolean setData(HypixelGames game) {
        this.isNicked = false;
        this.hasPlayed = false;
        JsonObject obj = null;
        boolean isFunctional = false;

        try {
            obj = getPlayerData(getPlayerUUID());
            isFunctional = true;
        } catch (ApiRequestException ignored) {
        } catch (PlayerNullException ex) {
            this.isNicked = true;
        } catch (InvalidKeyException ex) {
            ChatUtils.sendMessage(ChatColor.RED + "Invalid API Key");
        }

        try {
            if (!this.isNicked && isFunctional) {
                this.hasPlayed = true;
                this.playersJson = obj;
                return true;
            }
            return false;
        } catch (NullPointerException ex) {
            if (!this.isNicked) {
                System.err.println(String.format("%s Maybe %s has never played %s before", References.MODNAME, getPlayerName(), game.getGameName()));
            }

            System.err.println(References.MODNAME + " " + ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public String getFormattedStats() {
        return null;
    }

    @Override
    public HypixelGames getGame() {
        return HypixelGames.GENERAL;
    }

    @Override
    public List<Stat> getStatList() {
        return this.statList;
    }

    @Override
    public List<Stat> getFormattedStatList() {
        return this.formattedStatList;
    }

    @Override
    public void setFormattedStatList() {
        StatString level = new StatString("Level");
        level.setValue(ChatColor.GREEN + getNetworkLevel(((StatLong)this.exp).getValue()));
        formattedStatList.add(level);

        StatString achievementPoints = new StatString("AP");
        achievementPoints.setValue(ChatColor.GREEN + Long.toString(((StatLong)achpoints).getValue()));
        formattedStatList.add(achievementPoints);
    }

}