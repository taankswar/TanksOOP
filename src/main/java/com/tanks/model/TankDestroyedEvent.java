package com.tanks.model;

/**
 * Событие уничтожения танка.
 */
public record TankDestroyedEvent(String tankId) implements GameEvent {}