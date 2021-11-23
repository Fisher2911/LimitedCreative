/*
 * Copyright 2021 Fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

    public static final Message ALREADY_IN_CREATIVE =
            new Message("already-in-creative", "<red>You are already in limited creative");

    public static final Message BANNED_COMMAND =
            new Message("banned-command", "<red>You cannot use that command in limited creative!");

    public static final Message BANNED_BLOCK_PLACE =
            new Message("banned-block-place", "<red>You cannot place that block while in " +
                    "limited creative mode!");

    public static final Message BANNED_BLOCK_BREAK =
            new Message("banned-block-break", "<red>You cannot break that block while in " +
                    "limited creative mode!");

    public static final Message CANNOT_SPAWN_MOB =
            new Message("cannot-spawn-mob", "<red>You cannot spawn mobs while in " +
                    "limited creative mode!");

    public static final Message CANNOT_CLICK_WITH_ITEM =
            new Message("cannot-click-with-item", "<red>You cannot click with that item " +
                    "while in limited creative mode!");

    public static final Message CANNOT_CLICK_BLOCK =
            new Message("cannot-click-block", "<red>You cannot interact with that block " +
                    "while in limited creative!");


}
