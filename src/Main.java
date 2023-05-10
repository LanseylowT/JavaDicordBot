import commands.CommandManager;
import io.github.cdimascio.dotenv.Dotenv;
import listeners.EventListeners;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Main {
    private final ShardManager shardManager;
    private final Dotenv config;

    // Creating the constructor
    /**
    * Loads Environment Variables and builds the Bot Shard Manager
    * @throws LoginException occurs when bot token is invalid
    */
    public Main() throws LoginException {
        // Load all the Environment Variables in the project
        config = Dotenv.configure().load();
        // Setting up the Token for the Discord Bot
        String token = config.get("TOKEN");

        // Setting up the Shard Manager
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);

        // Enabling the necessary Intents for the bot
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES);

        builder.enableIntents(GatewayIntent.GUILD_MESSAGE_TYPING);
        builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        // Enabling Caches
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS);

        // Disabling Caches
//        builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);

        // Set the status for the bot
        builder.setStatus(OnlineStatus.ONLINE);

        // Setting up the activity for the bot
        builder.setActivity(Activity.listening("Your Mom's Moans"));

        // Build the actual Shard Manager for the Bot
        shardManager = builder.build();

        // Register Listeners
        shardManager.addEventListener(new EventListeners());
        shardManager.addEventListener(new CommandManager());
    }

    // Setting up the Config getters
    public Dotenv getConfig() {
        return config;
    }

    // Setting up the Shard Manager getters
    public ShardManager getShardManager() {
        return shardManager;
    }

    public static void main(String[] args) {
        try{
            Main bot = new Main();
        } catch (LoginException e){
            System.out.println("ERROR: Provided Bot Token is Invalid!");
        }
    }
}