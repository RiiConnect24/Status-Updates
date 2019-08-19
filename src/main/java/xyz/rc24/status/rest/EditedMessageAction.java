package xyz.rc24.status.rest;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.Route;
import net.dv8tion.jda.core.requests.restaction.MessageAction;

import java.util.List;

public class EditedMessageAction extends MessageAction
{
    private static final String CONTENT_TOO_BIG = String.format("A message may not exceed %d characters. Please limit your input!", Message.MAX_CONTENT_LENGTH);

    EditedMessageAction(JDA api, Route.CompiledRoute route, MessageChannel channel)
    {
        super(api, route, channel);
    }

    @Override
    protected void handleResponse(Response response, Request<Message> request)
    {
        if (response.isOk())
            request.onSuccess(null);
        else
            request.onFailure(response);
    }

    @Override
    public EditedMessageAction apply(final Message message)
    {
        if (message == null || message.getType() != MessageType.DEFAULT)
            return this;
        final List<MessageEmbed> embeds = message.getEmbeds();
        if (embeds != null && !embeds.isEmpty())
            embed(embeds.get(0));
        files.clear();

        return content(message.getContentRaw()).tts(message.isTTS());
    }

    @Override
    public EditedMessageAction tts(final boolean isTTS)
    {
        this.tts = isTTS;
        return this;
    }

    @Override
    public EditedMessageAction content(final String content)
    {
        if (content == null || content.isEmpty())
            this.content.setLength(0);
        else if (content.length() <= Message.MAX_CONTENT_LENGTH)
            this.content.replace(0, this.content.length(), content);
        else
            throw new IllegalArgumentException(CONTENT_TOO_BIG);
        return this;
    }

    @Override
    protected boolean hasPermission(Permission perm)
    {
        return true;
    }
}
