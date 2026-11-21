package com.tanks.model;

/**
 * Перемещение на указанное количество клеток.
 */
public record MoveAction(int cells) implements TurnAction {}