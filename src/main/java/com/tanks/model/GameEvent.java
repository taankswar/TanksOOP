package com.tanks.model;

/**
 * События игры. Механика возвращает события, а View их отображает.
 */
public sealed interface GameEvent
        permits MoveEvent, RotateEvent, ShootEvent, TankDestroyedEvent, GameOverEvent {
}