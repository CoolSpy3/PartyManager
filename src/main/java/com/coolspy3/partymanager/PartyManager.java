package com.coolspy3.partymanager;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.coolspy3.hypixelapi.APIConfig;
import com.mojang.brigadier.LiteralMessage;

import me.kbrewster.exceptions.APIException;
import me.kbrewster.mojangapi.MojangAPI;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.FriendsReply;
import net.hypixel.api.reply.FriendsReply.FriendShip;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedInEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("partymanager")
public class PartyManager {

    public static final String inviteRegex = "^-----------------------------\\n(\\[[a-zA-Z0-9_\\+]+\\] )?([a-zA-Z0-9_]+) has invited you to join their party!\\nYou have 60 seconds to accept\\. Click here to join!\\n-----------------------------$";
    public static final Pattern invitePattern = Pattern.compile(inviteRegex);
    public static final String joinRegex = "Friend > ([a-zA-Z0-9_]+) joined\\.";
    public static final Pattern joinPattern = Pattern.compile(joinRegex);
    public static final String leaveRegex = "^(\\[[a-zA-Z0-9_\\+]+\\] )?([a-zA-Z0-9_]+) has disconnected, they have 5 minutes to rejoin before they are removed from the party\\.$";
    public static final Pattern leavePattern = Pattern.compile(leaveRegex);
    private boolean isLoggedIn = false;

    public PartyManager() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        try {
            Config.load();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        MinecraftForge.EVENT_BUS.register(new AutoAcceptCommand());
        MinecraftForge.EVENT_BUS.register(new AutoPartyCommand());
        MinecraftForge.EVENT_BUS.register(new HelpCommand());
        MinecraftForge.EVENT_BUS.register(new KickOfflinePartyMembersCommand());
        MinecraftForge.EVENT_BUS.register(new PartyPresetClearCommand());
        MinecraftForge.EVENT_BUS.register(new PartyPresetCommand());
        MinecraftForge.EVENT_BUS.register(new PartyPresetListCommand());
        MinecraftForge.EVENT_BUS.register(new PartyPresetSetCommand());
    }

    @SubscribeEvent
    public void onChatMessageRecieved(ClientChatReceivedEvent event) {
        if (Config.getInstance().autoAcceptEnabled) {
            String msg = event.getMessage().getString();
            Matcher inviteMatcher = invitePattern.matcher(msg);
            if (inviteMatcher.matches()) {
                String player = inviteMatcher.group(2).toLowerCase();
                if (Config.getInstance().autoAcceptedPlayers.contains(player)) {
                    Minecraft.getInstance().player.chat("/p accept " + player);
                }
            }
        }
        if (Config.getInstance().autoInviteEnabled) {
            String msg = event.getMessage().getString();
            Matcher joinMatcher = joinPattern.matcher(msg);
            if (joinMatcher.matches()) {
                String player = joinMatcher.group(1).toLowerCase();
                if (Config.getInstance().autoInvitedPlayers.contains(player)) {
                    executeAsync(() -> {
                        try {Thread.sleep(3000);} catch(InterruptedException e) {}
                        Minecraft.getInstance().player.chat("/p " + player);
                    });
                }
            }
        }
        if (Config.getInstance().kickOfflinePartyMembers) {
            String msg = event.getMessage().getString();
            Matcher leaveMatcher = leavePattern.matcher(msg);
            if (leaveMatcher.matches()) {
                String player = leaveMatcher.group(2).toLowerCase();
                Minecraft.getInstance().player.chat("/p kick " + player);
            }
        }
    }

    @SubscribeEvent
    public void onServerJoined(LoggedInEvent event) {
        if (Config.getInstance().autoInviteEnabled && Config.getInstance().autoInvitedPlayers.size() != 0) {
            try {
                String apiKey = APIConfig.getInstance().getAPIKey();
                if (apiKey == null) {
                    sendMessage(TextFormatting.YELLOW
                            + "WARNING: Hypixel API is not linked! Some features may be disabled :(");
                    sendMessage(TextFormatting.YELLOW
                            + "Ensure that the Hypixel API mod is installed and run \"/linkhypixelapi\"");
                } else {
                    // Ensure that isLoggedIn is not affected within the async code
                    boolean isLoggedIn = this.isLoggedIn;
                    executeAsync(() -> {
                        HypixelAPI api = new HypixelAPI(UUID.fromString(apiKey));
                        if(!isLoggedIn) {
                            FriendsReply friendsReply = api.getFriends(Minecraft.getInstance().player.getUUID()).join();
                            for (UUID friend : getFriends(friendsReply.getFriendShips())) {
                                try {
                                    String username = MojangAPI.getName(friend);
                                    if(Config.getInstance().autoInvitedPlayers.contains(username.toLowerCase()) && api.getStatus(friend).join().getSession().isOnline()) {
                                        Minecraft.getInstance().player.chat("/p " + username);
                                    }
                                } catch(APIException | IOException e) {
                                    e.printStackTrace(System.err);
                                }
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        isLoggedIn = true;
    }

    @SubscribeEvent
    public void onServerLeft(LoggedOutEvent event) {
        isLoggedIn = false;
    }

    public static List<UUID> getFriends(List<FriendShip> friends) {
        UUID selfUUID = Minecraft.getInstance().player.getUUID();
        return friends.stream().map(friendship -> friendship.getUuidSender().equals(selfUUID) ? friendship.getUuidReceiver() : friendship.getUuidSender()).collect(Collectors.toList());
    }

    public static void executeAsync(Runnable function) {
        Thread thread = new Thread(function);
        thread.setDaemon(true);
        thread.start();
    }

    public static void sendMessage(String msg) {
        Minecraft.getInstance().player.sendMessage(TextComponentUtils.fromMessage(new LiteralMessage(msg)), Util.NIL_UUID);
    }
}
