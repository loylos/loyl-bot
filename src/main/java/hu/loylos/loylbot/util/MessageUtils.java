package hu.loylos.loylbot.util;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Permission;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.function.Predicate;

@UtilityClass
public class MessageUtils {

    public Predicate<Message> isBot = message -> message.getAuthor().isPresent() && message.getAuthor().get().isBot();

    public Predicate<Message> isHuman = message -> !isBot.test(message);

    public Mono<Boolean> botAdmin(Guild guild, User user) {
        return guild.getMemberById(user.getId())
                .flatMap(Member::getBasePermissions)
                .map(Collection::stream)
                .map(permissionStream -> permissionStream.anyMatch(permission -> permission.equals(Permission.ADMINISTRATOR) || permission.equals(Permission.MANAGE_GUILD)));
    }
}
