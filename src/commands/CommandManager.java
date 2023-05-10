package commands;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandManager extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        // Run the /welcome command
        if (command.equals("welcome")) {
            String userTag = event.getUser().getAsTag();
            // Reply with a message
            event.reply("Welcome to the server, **" + userTag + "**!").queue();
        } else if (command.equals("roles")) {
            // Run the /roles command
            event.deferReply().queue(); // So that the bot can wait more than 3 seconds for a reply on a command
            String response = "";
            // Loop through all the roles in the server
            for (Role role : event.getGuild().getRoles()) {
                response += role.getAsMention() + "\n"; // @Shiryo
            }
            event.getHook().sendMessage(response).queue(); // ONLY 1 REPLY PER SLASH COMMANDS
        } else if (command.equals("say")) {
            // Getting the options from the bot
            OptionMapping messageOption = event.getOption("message");
            OptionMapping channelOption = event.getOption("channel");
            assert messageOption != null;
            String message = messageOption.getAsString();

            MessageChannel channel;
            if (channelOption != null) {
                channel = channelOption.getAsChannel().asGuildMessageChannel();
            } else {
                channel = event.getChannel();
            }

            channel.sendMessage(message).queue();
            event.reply("Your message was sent!").setEphemeral(true).queue();
        } else if (command.equals("emote")) {
            MessageChannel channel;
            OptionMapping option = event.getOption("type");
            OptionMapping channelOption = event.getOption("channel");

            assert option != null;
            String type = option.getAsString();
            String replyMessage = "";
            String userTag = event.getUser().getAsMention();

            // Optional choice
            if (channelOption != null) {
                channel = channelOption.getAsChannel().asGuildMessageChannel();
            } else {
                channel = event.getChannel();
            }

            switch (type.toLowerCase()) {
                case "hug" -> {
                    replyMessage = "**" + userTag + "** hugged the closest person to him";
                }
                case "laugh" -> {
                    replyMessage = "**" + userTag + "** laughed so hard";
                }
                case "cry" -> {
                    replyMessage = "**" + userTag + "** cried a lot";
                }
            }

            channel.sendMessage(replyMessage).queue();
            event.reply("Your message was sent").setEphemeral(true).queue();
        }
    }
    // 2 ways to actually register the command to the discord bot

    // Guild Commands -- Instantly updates (max 100 guild commands)
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("welcome", "Get welcomed by the bot."));
        commandData.add(Commands.slash("roles", "Display all roles on the server."));

        // Command: /say <message> [channel]
        OptionData option1 = new OptionData(OptionType.STRING, "message", "The message you want the bot to say", true);
        OptionData option2 = new OptionData(OptionType.CHANNEL, "channel", "The channel you want to send this message in", false)
                .setChannelTypes(ChannelType.TEXT, ChannelType.NEWS, ChannelType.GUILD_PUBLIC_THREAD);
        OptionData option3 = new OptionData(OptionType.STRING, "type", "A type of emotion to express", true)
                .addChoice("Hug", "hug")
                .addChoice("Laugh", "laugh")
                .addChoice("Cry", "cry");

        commandData.add(Commands.slash("say", "Make the bot say a message").addOptions(option1, option2));

        // Emotes: /emote [type]
        commandData.add(Commands.slash("emote", "Express you emotions through text").addOptions(option3, option2));

        event.getGuild().updateCommands().addCommands(commandData).queue();

        // If the specific server is found, we will register special commands
        if (event.getGuild().getIdLong() == 1035205071190900736L) {
            System.out.println("Something you will do here");
        }
    }

    // So that everytime the bot joins another server/guild, it will also update/load the commands that are registered
    /*@Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("welcome", "Get welcomed by the bot."));
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }*/

    // Global Commands -- Up to an hour to update (unlimited)
    /*@Override
    public void onReady(@NotNull ReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("welcome", "Get welcomed by the bot."));
        event.getJDA().updateCommands().addCommands(commandData).queue(); // The only difference is the "getJDA" method is used
    }*/
}
