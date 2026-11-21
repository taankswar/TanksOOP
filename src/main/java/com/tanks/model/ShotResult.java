package com.tanks.model;

/**
 * Результат выстрела.
 */
public record ShotResult(
        boolean penetrated,    // пробил ли броню
        ArmorZone hitZone,     // зона попадания
        int damage,            // нанесенный урон
        int hpAfterShot,       // HP цели после выстрела
        boolean destroyed      // уничтожена ли цель
) {
}