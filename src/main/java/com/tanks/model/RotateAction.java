package com.tanks.model;

/**
 * Поворот в указанном направлении.
 */
public record RotateAction(Direction newDirection) implements TurnAction {}