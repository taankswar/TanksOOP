package com.tanks.model;

public class HeavyTank extends Tank {
    private static final TankStats STATS = new TankStats(
            200, 50, 75,
            80, 45, 30,
            2, 8
    );

    public HeavyTank(String id, Position position, Direction facing) {
        super(id, position, facing, STATS);
    }
}