package com.coolspy3.partymanager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PartyPresetCommand {
    
    public static final String regex = "/p([0-9][0-9]?[0-9]?)( .*)?";
    public static final Pattern pattern = Pattern.compile(regex);

    @SubscribeEvent
    public void register(ClientChatEvent event) {
        String msg = event.getMessage();
        Matcher matcher = pattern.matcher(msg);
        if(matcher.matches()) {
            event.setCanceled(true);
            Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
            Integer idx = Integer.parseInt(matcher.group(1));
            if(Config.getInstance().presets.containsKey(idx)) {
                Minecraft.getInstance().player.chat("/p " + Config.getInstance().presets.get(idx));
            } else {
                PartyManager.sendMessage(TextFormatting.RED + "Preset: " + idx + " is Undefined!");
            }
        }
    }

}
