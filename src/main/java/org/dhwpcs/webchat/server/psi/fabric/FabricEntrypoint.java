package org.dhwpcs.webchat.server.psi.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.dhwpcs.webchat.server.WebChatServer;
import org.dhwpcs.webchat.server.data.AccountInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FabricEntrypoint implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            WebChatServer.initialize(server, FabricGameApi.initialize(server));
            CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
            LiteralArgumentBuilder<ServerCommandSource> root = LiteralArgumentBuilder
                    .<ServerCommandSource>literal("webchat")
                    .requires(source -> source.getEntity() != null && source.getEntity() instanceof ServerPlayerEntity)

                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("register")
                            .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("password", StringArgumentType.string())
                                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("account", StringArgumentType.string())
                                            .executes(source -> register(source.getSource().getPlayer(), StringArgumentType.getString(source, "account"), StringArgumentType.getString(source, "password"))))
                                    .executes(source -> register(source.getSource().getPlayer(), StringArgumentType.getString(source, "password")))))

                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("changepwd")
                            .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("account", StringArgumentType.string())
                                    .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("newPwd", StringArgumentType.string())
                                            .executes(source -> changePassword(source.getSource().getPlayer(), StringArgumentType.getString(source, "account"), StringArgumentType.getString(source, "password"))))))

                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("queryInfo")
                            .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("account", StringArgumentType.string())
                                    .executes(source -> queryInfo(source.getSource().getPlayer(), StringArgumentType.getString(source, "account")))));
            dispatcher.register(root);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(WebChatServer::stop);
    }


    public static int register(ServerPlayerEntity source, String password) {
        return register(source, source.getGameProfile().getName(), password);
    }

    public static int register(ServerPlayerEntity source, String account, String password) {
        return WebChatServer.INSTANCE.run(it -> {
            Future<Map<String, AccountInfo>> future = it.getUserRegistry();
            if(!future.isDone()) {
                source.sendMessage(new LiteralText("The account info is not completely loaded yet! Please wait and perform again.").styled(style -> style.withColor(Formatting.RED)), false);
                return 1;
            } else {
                try {
                    Map<String, AccountInfo> map = future.get();
                    AccountInfo info = new AccountInfo(source.getGameProfile().getId(), source.getGameProfile().getName(), password);
                    if(map.putIfAbsent(account, info) != null) {
                        source.sendMessage(new LiteralText("The specified account already exists!").styled(style -> style.withColor(Formatting.RED)), false);
                        return 1;
                    } else {
                        source.sendMessage(new LiteralText("Successfully registered an account.").styled(style -> style.withColor(Formatting.GREEN)), false);
                        return 0;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    source.sendMessage(new LiteralText("An unexpected exception thrown when trying to change password:").styled(style -> style.withColor(Formatting.RED)), false);
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));
                    source.sendMessage(new LiteralText(writer.toString()).styled(style -> style.withColor(Formatting.RED)), false);
                    return 1;
                }
            }
        });
    }

    public static int changePassword(ServerPlayerEntity source, String account, String password) {
        return WebChatServer.INSTANCE.run(it -> {
            Future<Map<String, AccountInfo>> future = it.getUserRegistry();
            if(!future.isDone()) {
                source.sendMessage(new LiteralText("The account info is not completely loaded yet! Please wait and perform again.").styled(style -> style.withColor(Formatting.RED)), false);
                return 1;
            } else {
                try {
                    Map<String, AccountInfo> map = future.get();
                    AccountInfo infoOld = map.get(account);
                    if(infoOld == null) {
                        source.sendMessage(new LiteralText("The specified account does not exist!").styled(style -> style.withColor(Formatting.RED)), false);
                        return 1;
                    } else {
                        AccountInfo infoNew = new AccountInfo(infoOld.uid(), infoOld.name(), password);
                        if(map.replace(account, infoOld, infoNew)) {
                            source.sendMessage(new LiteralText("Your password has been changed successfully.").styled(style -> style.withColor(Formatting.GREEN)), false);
                            return 0;
                        } else {
                            source.sendMessage(new LiteralText("Sorry, but we can't change your password at this time.(Unexpected error)").styled(style -> style.withColor(Formatting.RED)), false);
                            return 1;
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    source.sendMessage(new LiteralText("An unexpected exception thrown when trying to change password:").styled(style -> style.withColor(Formatting.RED)), false);
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));
                    source.sendMessage(new LiteralText(writer.toString()).styled(style -> style.withColor(Formatting.RED)), false);
                    return 1;
                }
            }
        });
    }

    public static int queryInfo(ServerPlayerEntity source, String account) {
        return WebChatServer.INSTANCE.run(it -> {
            Future<Map<String, AccountInfo>> future = it.getUserRegistry();
            if(!future.isDone()) {
                source.sendMessage(new LiteralText("The account info is not completely loaded yet! Please wait and perform again.").styled(style -> style.withColor(Formatting.RED)), false);
                return 1;
            } else {
                try {
                    Map<String, AccountInfo> map = future.get();
                    AccountInfo infoOld = map.get(account);
                    if(infoOld == null) {
                        source.sendMessage(new LiteralText("The specified account does not exist!").styled(style -> style.withColor(Formatting.RED)), false);
                        return 1;
                    } else {
                        source.sendMessage(new LiteralText("Account: "+account), false);
                        source.sendMessage(new LiteralText("Nickname: "+infoOld.name()), false);
                        source.sendMessage(new LiteralText("UUID: "+infoOld.uid()), false);
                        return 0;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    source.sendMessage(new LiteralText("An unexpected exception thrown when trying to change password:").styled(style -> style.withColor(Formatting.RED)), false);
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));
                    source.sendMessage(new LiteralText(writer.toString()).styled(style -> style.withColor(Formatting.RED)), false);
                    return 1;
                }
            }
        });
    }

}
