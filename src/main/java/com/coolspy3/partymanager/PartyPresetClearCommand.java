package com.coolspy3.partymanager;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PartyPresetClearCommand {
    
    public static final String regex = "/pmclear ([0-9][0-9]?[0-9]?)( .*)?";
    public static final Pattern pattern = Pattern.compile(regex);

    @SubscribeEvent
    public void register(ClientChatEvent event) {
        try {
            String msg = event.getMessage();
            if(msg.startsWith("/pmclear ") || msg.equals("/pmclear")) {
                event.setCanceled(true);
                Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
                Matcher matcher = pattern.matcher(msg);
                if(matcher.matches()) {
                    Integer idx = Integer.parseInt(matcher.group(1));
                    Config.getInstance().presets.remove(idx);
                    PartyManager.sendMessage(TextFormatting.RED + "Preset Cleared!");
                    Config.save();
                } else {
                    PartyManager.sendMessage(TextFormatting.RED + "Usage: /pmclear <preset>");
                }
            }
        } catch(IOException e) {
            e.printStackTrace(System.err);
        }
    }

}
