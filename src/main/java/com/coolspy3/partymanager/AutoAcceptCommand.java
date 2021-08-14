package com.coolspy3.partymanager;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AutoAcceptCommand {
    
    private final int addLength = "/apa add ".length();
    private final int removeLength = "/apa remove ".length();

    @SubscribeEvent
    public void register(ClientChatEvent event) {
        try {
            String msg = event.getMessage();
            if(msg.startsWith("/apa ")) {
                event.setCanceled(true);
                Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
                if(msg.startsWith("/apa add ")) {
                    if(msg.matches("/apa add [a-zA-Z0-9_]+")) {
                        String player = msg.substring(addLength).toLowerCase();
                        if(!Config.getInstance().autoAcceptedPlayers.contains(player)) {
                            Config.getInstance().autoAcceptedPlayers.add(player);
                        }
                        PartyManager.sendMessage(TextFormatting.AQUA + "Auto Accepting Party Requests From: \"" + player + "\"");
                        Config.save();
                    } else {
                        PartyManager.sendMessage(TextFormatting.RED + "Invalid Username: \"" + msg.substring(addLength).toLowerCase() + "\"");
                    }
                } else if(msg.startsWith("/apa remove ")) {
                    if(msg.matches("/apa remove [a-zA-Z0-9_]+")) {
                        String player = msg.substring(removeLength).toLowerCase();
                        if(Config.getInstance().autoAcceptedPlayers.contains(player)) {
                            Config.getInstance().autoAcceptedPlayers.remove(player);
                        }
                        PartyManager.sendMessage(TextFormatting.RED + "No Longer Auto Accepting Party Requests From: \"" + player + "\"");
                        Config.save();
                    } else {
                        PartyManager.sendMessage(TextFormatting.RED + "Invalid Username: \"" + msg.substring(removeLength).toLowerCase() + "\"");
                    }
                } else if(msg.startsWith("/apa list")) {
                    PartyManager.sendMessage(TextFormatting.AQUA + "Auto Accepting Party Requests From:");
                    if(Config.getInstance().autoAcceptedPlayers.size() == 0) {
                        PartyManager.sendMessage(TextFormatting.AQUA + "<Nobody>");
                    } else {
                        for(String player: Config.getInstance().autoAcceptedPlayers) {
                            PartyManager.sendMessage(TextFormatting.AQUA + player);
                        }
                    }
                } else {
                    PartyManager.sendMessage(TextFormatting.RED + "Usage: /apa [add | remove | list] <player>");
                }
            } else if(msg.equals("/apa")) {
                event.setCanceled(true);
                Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
                Config.getInstance().autoAcceptEnabled = !Config.getInstance().autoAcceptEnabled;
                if(Config.getInstance().autoAcceptEnabled) {
                    PartyManager.sendMessage(TextFormatting.AQUA + "Auto Party Accept Enabled!");
                } else {
                    PartyManager.sendMessage(TextFormatting.RED + "Auto Party Accept Disabled!");
                }
                Config.save();
            }
        } catch(IOException e) {
            e.printStackTrace(System.err);
        }
    }

}
