package com.tanks.model;

public class MediumTank extends Tank {
    private static final TankStats STATS = new TankStats(
            140, 35, 55,
            50, 30, 20,
            3, 10
    );

    public MediumTank(String id, Position position, Direction facing) {
        super(id, position, facing, STATS);
    }
}