package com.tanks.model;

/**
 * Характеристики класса техники.
 * Это неизменяемый объект (record), который задается при создании танка.
 */
public record TankStats(
        int maxHp,
        int gunDamage,
        int gunPenetration,
        int frontArmor,
        int sideArmor,
        int rearArmor,
        int mobility,
        int maxAmmo
) {
    /**
     * Возвращает значение брони для конкретной зоны.
     */
    public int getArmor(ArmorZone zone) {
        return switch (zone) {
            case FRONT -> frontArmor;
            case LEFT_SIDE, RIGHT_SIDE -> sideArmor;
            case REAR -> rearArmor;
        };
    }
}