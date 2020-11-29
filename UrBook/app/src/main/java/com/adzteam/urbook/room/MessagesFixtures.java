package com.adzteam.urbook.room;

import com.adzteam.urbook.room.model.Message;
import com.adzteam.urbook.room.model.User;

import java.util.UUID;

public final class MessagesFixtures {
    private MessagesFixtures() {
        throw new AssertionError();
    }

    public static Message getTextMessage(String text) {
        return new Message(Long.toString(UUID.randomUUID().getLeastSignificantBits()), getUser(), text);
    }

    private static User getUser() {
        return new User(
                "0",
                "Tester",
                "none");
    }
}
