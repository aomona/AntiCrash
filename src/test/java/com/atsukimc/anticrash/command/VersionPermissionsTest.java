//? if >=1.21.11 {
package com.atsukimc.anticrash.command;
//? if >=26.1 {
import net.minecraft.server.permissions.*;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.PermissionSet;
//?} else if =1.21.11 {
//import net.minecraft.command.permission.*;
//?}
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class VersionPermissionsTest {
    @Test void allConfiguredLevelsPreserveMinimumLevelAndNonOpStart() {
        for(int actor=0;actor<=4;actor++) for(int required=0;required<=4;required++)
//? if >=26.1 {
            assertEquals(actor>=required,VersionPermissions.hasLevel(LevelBasedPermissionSet.forLevel(PermissionLevel.byId(actor)),required));
        assertTrue(VersionPermissions.hasLevel(PermissionSet.NO_PERMISSIONS,0));
        assertFalse(VersionPermissions.hasLevel(PermissionSet.NO_PERMISSIONS,1));
//?} else if =1.21.11 {
//            assertEquals(actor>=required,VersionPermissions.hasLevel(LeveledPermissionPredicate.fromLevel(PermissionLevel.fromLevel(actor)),required));
//        assertTrue(VersionPermissions.hasLevel(PermissionPredicate.NONE,0));
//        assertFalse(VersionPermissions.hasLevel(PermissionPredicate.NONE,1));
//?}
    }
    @Test void invalidLevelsNeverClampToAnAllowedPermission() {
//? if >=26.1 {
        assertFalse(VersionPermissions.hasLevel(PermissionSet.ALL_PERMISSIONS,-1));
        assertFalse(VersionPermissions.hasLevel(PermissionSet.ALL_PERMISSIONS,5));
        assertFalse(VersionPermissions.hasLevel((PermissionSet)null,0));
//?} else if =1.21.11 {
//        assertFalse(VersionPermissions.hasLevel(PermissionPredicate.ALL,-1));
//        assertFalse(VersionPermissions.hasLevel(PermissionPredicate.ALL,5));
//        assertFalse(VersionPermissions.hasLevel((PermissionPredicate)null,0));
//?}
    }
}
//?}
