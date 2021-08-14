package com.coolspy3.partymanager;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AutoPartyCommand {
    
    private final int addLength = "/ap add ".length();
    private final int removeLength = "/ap remove ".length();

    @SubscribeEvent
    public void register(ClientChatEvent event) {
        try {
            String msg = event.getMessage();
            if(msg.startsWith("/ap ")) {
                event.setCanceled(true);
                Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
                if(msg.startsWith("/ap add ")) {
                    if(msg.matches("/ap add [a-zA-Z0-9_]+")) {
                        String player = msg.substring(addLength).toLowerCase();
                        if(!Config.getInstance().autoInvitedPlayers.contains(player)) {
                            Config.getInstance().autoInvitedPlayers.add(player);
                        }
                        PartyManager.sendMessage(TextFormatting.AQUA + "Auto Inviting: \"" + player + "\"");
                        Config.save();
                    } else {
                        PartyManager.sendMessage(TextFormatting.RED + "Invalid Username: \"" + msg.substring(addLength).toLowerCase() + "\"");
                    }
                } else if(msg.startsWith("/ap remove ")) {
                    if(msg.matches("/ap remove [a-zA-Z0-9_]+")) {
                        String player = msg.substring(removeLength).toLowerCase();
                        if(Config.getInstance().autoInvitedPlayers.contains(player)) {
                            Config.getInstance().autoInvitedPlayers.remove(player);
                        }
                        PartyManager.sendMessage(TextFormatting.RED + "No Longer Auto Inviting: \"" + player + "\"");
                        Config.save();
                    } else {
                        PartyManager.sendMessage(TextFormatting.RED + "Invalid Username: \"" + msg.substring(removeLength).toLowerCase() + "\"");
                    }
                } else if(msg.startsWith("/ap list")) {
                    PartyManager.sendMessage(TextFormatting.AQUA + "Auto Inviting:");
                    if(Config.getInstance().autoInvitedPlayers.size() == 0) {
                        PartyManager.sendMessage(TextFormatting.AQUA + "<Nobody>");
                    } else {
                        for(String player: Config.getInstance().autoInvitedPlayers) {
                            PartyManager.sendMessage(TextFormatting.AQUA + player);
                        }
                    }
                } else {
                    PartyManager.sendMessage(TextFormatting.RED + "Usage: /ap [add | remove | list] <player>");
                }
            } else if(msg.equals("/ap")) {
                event.setCanceled(true);
                Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
                Config.getInstance().autoInviteEnabled = !Config.getInstance().autoInviteEnabled;
                if(Config.getInstance().autoInviteEnabled) {
                    PartyManager.sendMessage(TextFormatting.AQUA + "Auto Invite Enabled!");
                } else {
                    PartyManager.sendMessage(TextFormatting.RED + "Auto Invite Disabled!");
                }
                Config.save();
            }
        } catch(IOException e) {
            e.printStackTrace(System.err);
        }
    }

}
