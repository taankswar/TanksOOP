package com.tanks.model;

/**
 * Событие поворота танка.
 */
public record RotateEvent(String tankId, Direction oldFacing, Direction newFacing) implements GameEvent {}