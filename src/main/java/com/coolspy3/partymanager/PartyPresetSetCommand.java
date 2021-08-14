package com.coolspy3.partymanager;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PartyPresetSetCommand {
    
    public static final String regex = "/pmset ([0-9][0-9]?[0-9]?) ([a-zA-Z0-9_]+)( .*)?";
    public static final Pattern pattern = Pattern.compile(regex);

    @SubscribeEvent
    public void register(ClientChatEvent event) {
        try {
            String msg = event.getMessage();
            if(msg.startsWith("/pmset ") || msg.equals("/pmset")) {
                event.setCanceled(true);
                Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
                Matcher matcher = pattern.matcher(msg);
                if(matcher.matches()) {
                    Integer idx = Integer.parseInt(matcher.group(1));
                    String player = matcher.group(2);
                    Config.getInstance().presets.put(idx, player);
                    PartyManager.sendMessage(TextFormatting.AQUA + "Preset Set!");
                    Config.save();
                } else {
                    PartyManager.sendMessage(TextFormatting.RED + "Usage: /pmset <preset> <player>");
                }
            }
        } catch(IOException e) {
            e.printStackTrace(System.err);
        }
    }

}
