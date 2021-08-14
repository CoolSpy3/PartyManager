package com.coolspy3.partymanager;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KickOfflinePartyMembersCommand {

    @SubscribeEvent
    public void register(ClientChatEvent event) {
        try {
            String msg = event.getMessage();
            if(msg.matches("/kopm( .*)?")) {
                event.setCanceled(true);
                Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
                Config.getInstance().kickOfflinePartyMembers = !Config.getInstance().kickOfflinePartyMembers;
                if(Config.getInstance().kickOfflinePartyMembers) {
                    PartyManager.sendMessage(TextFormatting.AQUA + "Kick Offline Party Members Enabled!");
                } else {
                    PartyManager.sendMessage(TextFormatting.RED + "Kick Offline Party Members Disabled!");
                }
                Config.save();
            }
        } catch(IOException e) {
            e.printStackTrace(System.err);
        }
    }

}
