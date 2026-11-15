package com.pvpmaster.entity;

/** Difficulty presets. No cheating: reaction delay and miss chance are always applied. */
public enum BotLevel {
    //     reaction, accuracy, interval, strafe, jump,  retreat, useItems, speed
    EASY(   14, 0.55f, 26, false, false, false, false, 0.90f),
    NORMAL(  8, 0.75f, 16, true,  false, false, false, 1.00f),
    HARD(    4, 0.90f, 12, true,  true,  true,  true,  1.05f),
    EXPERT(  2, 0.97f, 12, true,  true,  true,  true,  1.10f);

    public final int reactionTicks;
    public final float accuracy;
    public final int attackInterval;
    public final boolean strafe, jump, retreat, useItems;
    public final float speed;

    BotLevel(int r, float a, int i, boolean s, boolean j, boolean ret, boolean items, float sp) {
        this.reactionTicks = r; this.accuracy = a; this.attackInterval = i;
        this.strafe = s; this.jump = j; this.retreat = ret; this.useItems = items; this.speed = sp;
    }

    public static BotLevel parse(String s) {
        for (BotLevel l : values()) if (l.name().equalsIgnoreCase(s)) return l;
        return null;
    }
}
