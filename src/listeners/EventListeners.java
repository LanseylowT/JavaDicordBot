package listeners;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EventListeners extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        // If reaction is added, send a message to the server

        User user = event.getUser();
        String emoji = event.getReaction().getEmoji().getAsReactionCode();
        String channelMention = event.getChannel().getAsMention();
        String jumpLink = event.getJumpUrl();
        TextChannel textChannel = event.getGuild().getTextChannelsByName("general", true).get(0);

        assert user != null;
        String message = user.getAsTag() + " reacted to a message with " + emoji + " in the " + channelMention + " channel!";

        // Send it back to the Channel
        // Sending to specific channel by name
        textChannel.sendMessage(message).queue();

        // Sending to current channel of the event
//        event.getGuildChannel().sendMessage(message).queue();
//        event.getChannel().sendMessage(message).queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();

        if (message.contains("ping")) {
            event.getChannel().sendMessage("pong").queue();
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getUser();
        String avatar = event.getUser().getEffectiveAvatarUrl();
        TextChannel textChannel = event.getGuild().getTextChannelsByName("welcome", true).get(0);

        // Messages
        String message = String.format("Welcome to the [Server] %s:  %s", event.getGuild(), user);

        // Send the message
        textChannel.sendMessage(message).queue();
    }

    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
        List<Member> members = event.getGuild().getMembers();
        int onlineMembers = 0;

        for (Member member : members){
            if (member.getOnlineStatus() == OnlineStatus.ONLINE){
                onlineMembers++;
            }
        }

        User user = event.getUser();
        TextChannel textChannel = event.getGuild().getTextChannelsByName("general", true).get(0);
        String message = "**" + user.getAsTag() + "** updated their online status! There are now " + onlineMembers + " users online on the server!";
        textChannel.sendMessage(message).queue();
//        Objects.requireNonNull(event.getGuild().getDefaultChannel()).asTextChannel().sendMessage(message).queue();
    }
}
