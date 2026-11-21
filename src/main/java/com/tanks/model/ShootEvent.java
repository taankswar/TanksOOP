package com.tanks.model;

/**
 * Событие выстрела.
 */
public record ShootEvent(
        String shooterId,
        String targetId,
        ArmorZone hitZone,
        int damage,
        boolean penetrated,
        boolean destroyed
) implements GameEvent {}