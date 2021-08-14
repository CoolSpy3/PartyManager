package com.coolspy3.partymanager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PartyPresetListCommand {
    
    public static final String regex = "/pmlist ([0-9][0-9]?[0-9]?)( .*)?";
    public static final Pattern pattern = Pattern.compile(regex);

    @SubscribeEvent
    public void register(ClientChatEvent event) {
        String msg = event.getMessage();
        if(msg.startsWith("/pmlist ") || msg.equals("/pmlist")) {
            event.setCanceled(true);
            Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
            Matcher matcher = pattern.matcher(msg);
            if(matcher.matches()) {
                Integer idx = Integer.parseInt(matcher.group(1));
                if(Config.getInstance().presets.containsKey(idx)) {
                    PartyManager.sendMessage(TextFormatting.AQUA + "Preset: " + idx + " is set to player: \"" + Config.getInstance().presets.get(idx) + "\"");
                }
            } else {
                PartyManager.sendMessage(TextFormatting.AQUA + "Presets:");
                if(Config.getInstance().presets.isEmpty()) {
                    PartyManager.sendMessage(TextFormatting.AQUA + "<None>");
                } else {
                    for(int idx: Config.getInstance().presets.keySet()) {
                        PartyManager.sendMessage(TextFormatting.AQUA + "" + idx + ": " + Config.getInstance().presets.get(idx));
                    }
                }
            }
        }
    }

}
