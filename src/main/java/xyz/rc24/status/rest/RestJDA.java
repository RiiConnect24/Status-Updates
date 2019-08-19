package xyz.rc24.status.rest;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.TextChannelImpl;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.Route;
import net.dv8tion.jda.core.utils.Checks;
import net.dv8tion.jda.core.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;

import javax.annotation.CheckReturnValue;
import java.util.EnumSet;

public class RestJDA
{
    private final JDAImpl fakeJDA;

    public RestJDA(String token, OkHttpClient client)
    {
        fakeJDA = new JDAImpl(AccountType.BOT, // AccountType accountType
                token, // String token
                null, // SessionController controller
                client, // OkHttpClient httpClient
                null, // WebSocketFactory wsFactory
                null, // ScheduledThreadPoolExecutor rateLimitPool
                null, // ScheduledExecutorService gatewayPool
                null, // ExecutorService callbackPool
                false, // boolean autoReconnect
                false, // boolean audioEnabled
                false, // boolean useShutdownHook
                false, // boolean bulkDeleteSplittingEnabled
                true, // boolean retryOnTimeout
                false, // boolean enableMDC
                true, // boolean shutdownRateLimitPool
                true, // boolean shutdownGatewayPool
                true, // boolean shutdownCallbackPool
                5, // int poolSize
                900, // int maxReconnectDelay
                null, // ConcurrentMap<String, String> contextMap
                EnumSet.allOf(CacheFlag.class)); // EnumSet<CacheFlag> cacheFlags
    }

    @CheckReturnValue
    public EditedMessageAction editMessage(long channelId, long messageId, Message newContent)
    {
        Checks.notNull(newContent, "message");
        Route.CompiledRoute route = Route.Messages.EDIT_MESSAGE.compile(Long.toString(channelId), Long.toString(messageId));
        return new EditedMessageAction(fakeJDA, route, new TextChannelImpl(channelId, new GuildImpl(fakeJDA, 0))).apply(newContent);
    }

    @CheckReturnValue
    public EditedMessageAction sendMessage(long channelId, Message msg)
    {
        Checks.notNull(msg, "Message");
        Route.CompiledRoute route = Route.Messages.SEND_MESSAGE.compile(Long.toString(channelId));
        return new EditedMessageAction(fakeJDA, route, new TextChannelImpl(channelId, new GuildImpl(fakeJDA, 0))).apply(msg);
    }
}
