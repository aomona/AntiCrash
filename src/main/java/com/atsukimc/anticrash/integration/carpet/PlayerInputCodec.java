//? if >=1.21.2 {
package com.atsukimc.anticrash.integration.carpet;

//? if (>=1.21.2 && <=1.21.11) {
import net.minecraft.util.PlayerInput;
//?} else if >=26.1 {
//import net.minecraft.world.entity.player.Input;
//?}

/** Preserve opposite keys too: reducing to a signed movement axis loses runtime state. */
public final class PlayerInputCodec {
    private PlayerInputCodec() { }
//? if (>=1.21.6 && <=1.21.11) {
    /** Since 1.21.6 sneak changes are in this packet, including key release. */
    public static boolean allowedDuringFreeze(PlayerInput current, PlayerInput next, boolean movement, boolean sneak) {
        int changed = capture(current) ^ capture(next);
        return (movement || (changed & 95) == 0) && (sneak || (changed & 32) == 0);
    }
    public static int capture(PlayerInput input) {
//?} else if (>=1.21.2 && <=1.21.5) {
//    public static int capture(PlayerInput input) {
//?} else if >=26.1 {
//    /** Since 1.21.6 sneak changes are in this packet, including key release. */
//    public static boolean allowedDuringFreeze(Input current, Input next, boolean movement, boolean sneak) {
//        int changed = capture(current) ^ capture(next);
//        return (movement || (changed & 95) == 0) && (sneak || (changed & 32) == 0);
//    }
//    public static int capture(Input input) {
//?}
        return (input.forward()?1:0)|(input.backward()?2:0)|(input.left()?4:0)|(input.right()?8:0)
//? if (>=1.21.2 && <=1.21.11) {
                |(input.jump()?16:0)|(input.sneak()?32:0)|(input.sprint()?64:0);
//?} else if >=26.1 {
//                |(input.jump()?16:0)|(input.shift()?32:0)|(input.sprint()?64:0);
//?}
    }
//? if (>=1.21.2 && <=1.21.11) {
    public static PlayerInput restore(Integer flags) {
//?} else if >=26.1 {
//    public static Input restore(Integer flags) {
//?}
        if (flags == null || flags < 0 || flags > 127)
            throw new IllegalArgumentException("Missing or invalid 1.21.2 PlayerInput state");
//? if (>=1.21.2 && <=1.21.11) {
        return new PlayerInput((flags&1)!=0,(flags&2)!=0,(flags&4)!=0,(flags&8)!=0,
//?} else if >=26.1 {
//        return new Input((flags&1)!=0,(flags&2)!=0,(flags&4)!=0,(flags&8)!=0,
//?}
                (flags&16)!=0,(flags&32)!=0,(flags&64)!=0);
    }
}
//?}
