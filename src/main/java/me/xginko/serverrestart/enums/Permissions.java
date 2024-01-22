package me.xginko.serverrestart.enums;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum Permissions {
    VERSION(new Permission("simplerestarts.cmd.version",
            "Permission get the plugin version", PermissionDefault.FALSE)),
    RELOAD(new Permission("simplerestarts.cmd.reload",
            "Permission to reload the plugin config", PermissionDefault.FALSE)),
    DISABLE(new Permission("simplerestarts.cmd.disable",
            "Permission to disable the plugin", PermissionDefault.FALSE));

    private final Permission permission;

    Permissions(Permission permission) {
        this.permission = permission;
    }

    public Permission get() {
        return permission;
    }
}