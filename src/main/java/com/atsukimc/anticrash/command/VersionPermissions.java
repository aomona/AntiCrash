//? if >=1.21.11 {
package com.atsukimc.anticrash.command;

//? if >=26.1 {
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.PermissionSet;
//?} else if =1.21.11 {
//import net.minecraft.command.permission.Permission;
//import net.minecraft.command.permission.PermissionLevel;
//import net.minecraft.command.permission.PermissionPredicate;
//import net.minecraft.server.command.ServerCommandSource;
//?}

/** Preserve configurable 0..4 command levels using Vanilla's new permission predicate. */
public final class VersionPermissions {
    private VersionPermissions() { }
//? if >=26.1 {
    public static boolean hasLevel(CommandSourceStack source,int level) {
        return hasLevel(source.permissions(),level);
//?} else if =1.21.11 {
//    public static boolean hasLevel(ServerCommandSource source,int level) {
//        return hasLevel(source.getPermissions(),level);
//?}
    }
//? if >=26.1 {
    public static boolean hasLevel(PermissionSet permissions,int level) {
//?} else if =1.21.11 {
//    public static boolean hasLevel(PermissionPredicate permissions,int level) {
//?}
        if(level<0 || level>4 || permissions==null) return false;
//? if >=26.1 {
        return level==0 || permissions.hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(level)));
//?} else if =1.21.11 {
//        return level==0 || permissions.hasPermission(new Permission.Level(PermissionLevel.fromLevel(level)));
//?}
    }
}
//?}
