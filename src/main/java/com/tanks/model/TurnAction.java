package com.tanks.model;

/**
 * Действия, которые может выполнить танк за ход.
 */
public sealed interface TurnAction permits MoveAction, RotateAction, ShootAction, PassAction {}