package com.coolspy3.partymanager;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HelpCommand {
    
    public static final String justABunchOfDashes = "-----------------------------";

    @SubscribeEvent
    public void register(ClientChatEvent event) {
        String msg = event.getMessage();
        if(msg.matches("/pmhelp( .*)?")) {
            event.setCanceled(true);
            Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
            PartyManager.sendMessage(TextFormatting.BLUE + justABunchOfDashes);
            PartyManager.sendMessage(TextFormatting.YELLOW + "/ap" + TextFormatting.AQUA + " - Toggle Party Auto Invite");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/ap add [username]" + TextFormatting.AQUA + " - Auto Invite the Specified User When They Are Online (This only fully works with friends)");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/ap remove [username]" + TextFormatting.AQUA + " - Stop Auto Inviting The Specified User");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/ap list"  + TextFormatting.AQUA + " - Lists The Players Who Will Be Auto Invited");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/apa" + TextFormatting.AQUA + " - Toggle Party Request Auto Accept");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/apa add [username]" + TextFormatting.AQUA + " - Auto Accept Party Requests From The Specified User");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/apa remove [username]" + TextFormatting.AQUA + " - Stop Auto Accepting Party Requests From The Specified User");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/apa list"  + TextFormatting.AQUA + " - Lists The Players Whose Party Requests Will Be Auto Accepted");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/kopm" + TextFormatting.AQUA + " - Toggle Auto Kick Of Offline Party Members");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/p[num]" + TextFormatting.AQUA + " - Party The Specified Preset Player (0-999)");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/pmclear [num]" + TextFormatting.AQUA + " - Clear The Specified Player Preset (0-999)");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/pmhelp" + TextFormatting.AQUA + " - Show This Help Menu");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/pmlist" + TextFormatting.AQUA + " - List All Party Presets");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/pmlist [num]" + TextFormatting.AQUA + " - List The Player Associated With Party Preset #[num] (0-999)");
            PartyManager.sendMessage(TextFormatting.YELLOW + "/pmset [num] [username]" + TextFormatting.AQUA + " - Sets The Player Associated With Party Preset #[num] (0-999)");
            PartyManager.sendMessage(TextFormatting.BLUE + justABunchOfDashes);
        }
    }

}
