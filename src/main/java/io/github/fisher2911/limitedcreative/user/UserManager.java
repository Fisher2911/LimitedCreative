package io.github.fisher2911.limitedcreative.user;

import com.sun.security.auth.UnixNumericUserPrincipal;
import io.github.fisher2911.fishcore.manager.Manager;
import io.github.fisher2911.limitedcreative.LimitedCreative;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager extends Manager<UUID, User> {

    private final LimitedCreative plugin;

    private final Map<UUID, User> users = new HashMap<>();

    private UserManager(final LimitedCreative plugin) {
        this.plugin = plugin;
    }

    public UserManager(final Map<UUID, User> map, final LimitedCreative plugin) {
        super(map);
        this.plugin = plugin;
    }

    @Nullable
    public User getUser(final UUID uuid) {
        return this.users.get(uuid);
    }

    // todo
    public void loadUser(final UUID uuid) {
        this.users.put(uuid, new User(uuid));
    }

    public void saveAllUsers() {
        for (final User user : this.users.values()) {
            this.saveUser(user);
        }
    }

    public void saveUser(final UUID uuid) {
        final User user = this.getUser(uuid);

        if (user == null) {
            return;
        }

        this.saveUser(user);
    }

    // todo
    public void saveUser(final User user) {

    }

}
