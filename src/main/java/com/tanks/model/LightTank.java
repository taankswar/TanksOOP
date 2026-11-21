package com.tanks.model;

public class LightTank extends Tank {
    private static final TankStats STATS = new TankStats(
            100, 25, 40, // HP, Урон, Пробитие
            30, 15, 10,  // Лоб, Борт, Корма
            5, 12        // Подвижность, Боезапас
    );

    public LightTank(String id, Position position, Direction facing) {
        super(id, position, facing, STATS);
    }
}