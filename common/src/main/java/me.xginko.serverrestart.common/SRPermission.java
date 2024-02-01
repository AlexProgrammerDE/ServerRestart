package me.xginko.serverrestart.common;

public enum SRPermission {

    VERSION("serverrestart.cmd.version", "Permission to get the plugin version", false),
    RELOAD("serverrestart.cmd.reload", "Permission to reload the plugin config", false),
    DISABLE("serverrestart.cmd.disable", "Permission to disable the plugin", false);

    private final String permission, description;
    private final boolean permDef;

    SRPermission(String permission, String description, boolean permissionDefault) {
        this.permission = permission;
        this.description = description;
        this.permDef = permissionDefault;
    }

    public String permission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    public boolean getDefault() {
        return permDef;
    }
}
