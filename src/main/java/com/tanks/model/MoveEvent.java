package com.tanks.model;

/**
 * Событие перемещения танка.
 */
public record MoveEvent(String tankId, Position from, Position to) implements GameEvent {}