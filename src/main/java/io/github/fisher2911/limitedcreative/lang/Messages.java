package io.github.fisher2911.limitedcreative.lang;

import io.github.fisher2911.fishcore.message.Message;

public class Messages {

    public static final Message INVALID_COMMAND_DEFAULT_FORMAT =
        new Message("invalid-command-default-format", "<red>Invalid default command format!");

    public static final Message MUST_BE_PLAYER =
            new Message("must-be-player", "<red>You must be a player to do this!");

    public static final Message NO_PERMISSION =
            new Message("no-permission", "<red>You do not have permission to do this!");

    public static final Message SET_TO_CREATIVE =
            new Message("set-to-creative", "<green>You have been set to creative mode!");

    public static final Message SET_TO_SURVIVAL =
            new Message("set-to-survival", "<green>You have been set to survival mode!");
}
