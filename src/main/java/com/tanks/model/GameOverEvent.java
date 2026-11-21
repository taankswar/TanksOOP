package com.tanks.model;

/**
 * Событие окончания игры.
 */
public record GameOverEvent(String winnerId) implements GameEvent {}