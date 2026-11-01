//? if >=1.18 {
package com.atsukimc.anticrash.integration.carpet;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.*;
import static org.junit.jupiter.api.Assertions.*;

class ProfileCacheBytecodeContractTest {
    @Test
    void injectionMatchesTheExactTargetVersion() throws Exception {
//? if (>=1.18 && <=1.21.11) {
        try (var input = getClass().getClassLoader().getResourceAsStream("net/minecraft/util/UserCache.class")) {
//?} else if >=26.1 {
//        try (var input = getClass().getClassLoader().getResourceAsStream("net/minecraft/server/players/CachedUserNameToIdResolver.class")) {
//?}
            assertNotNull(input, "The exact mapped game class must be present");
            boolean[] found = {false};
            new ClassReader(input).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override public MethodVisitor visitMethod(int access, String name, String descriptor,
                        String signature, String[] exceptions) {
//? if (>=1.18 && <=1.21.11) {
                    if (name.equals("findByName") && descriptor.equals("(Ljava/lang/String;)Ljava/util/Optional;")) found[0] = true;
//?} else if >=26.1 {
//                    if (name.equals("get") && descriptor.equals("(Ljava/lang/String;)Ljava/util/Optional;")) found[0] = true;
//?}
                    return null;
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            assertTrue(found[0], "UserCacheMixin must target a method that really exists");
        }
    }
}
//?}
