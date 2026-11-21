package com.tanks.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void testReproducibility_SameSeedSameMap() {
        // Главное требование: один и тот же seed дает одну и ту же карту
        Game game1 = new Game(12345L, 20, 20);
        Game game2 = new Game(12345L, 20, 20);

        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                assertEquals(
                        game1.getBoard().getCell(x, y),
                        game2.getBoard().getCell(x, y),
                        "Карты должны быть идентичны при одинаковом seed"
                );
            }
        }
    }

    @Test
    void testDifferentSeedDifferentMap() {
        Game game1 = new Game(111L, 20, 20);
        Game game2 = new Game(222L, 20, 20);

        // Найдем хотя бы одну клетку, которая отличается (вероятность 100%)
        boolean hasDifference = false;
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                if (game1.getBoard().getCell(x, y) != game2.getBoard().getCell(x, y)) {
                    hasDifference = true;
                    break;
                }
            }
        }
        assertTrue(hasDifference, "Карты с разным seed должны отличаться");
    }

    @Test
    void testAddTankOnEmptyCell() {
        Game game = new Game(42L, 20, 20);

        // Очищаем клетку для гарантии
        game.getBoard().setCell(0, 0, Board.EMPTY);

        Tank tank = new LightTank("T1", new Position(0, 0), Direction.UP);

        assertTrue(game.addTank(tank));
        assertEquals(1, game.getTanks().size());
    }

    @Test
    void testAddTankOnObstacleFails() {
        Game game = new Game(42L, 20, 20);

        // Принудительно ставим препятствие
        game.getBoard().setCell(5, 5, Board.OBSTACLE);

        Tank tank = new LightTank("T1", new Position(5, 5), Direction.UP);
        assertFalse(game.addTank(tank));
        assertEquals(0, game.getTanks().size());
    }

    @Test
    void testAddTankOnAnotherTankFails() {
        Game game = new Game(42L, 20, 20);

        // Принудительно очищаем клетку, чтобы гарантировать пустое место
        game.getBoard().setCell(5, 5, Board.EMPTY);

        Tank tank1 = new LightTank("T1", new Position(5, 5), Direction.UP);
        Tank tank2 = new MediumTank("T2", new Position(5, 5), Direction.DOWN);

        assertTrue(game.addTank(tank1));
        assertFalse(game.addTank(tank2)); // Должно вернуть false
    }

    @Test
    void testMoveTank_Forward() {
        Game game = new Game(42L, 20, 20);
        game.getBoard().setCell(0, 0, Board.EMPTY);

        Tank tank = new LightTank("T1", new Position(0, 0), Direction.RIGHT);
        game.addTank(tank);

        assertTrue(game.moveTank("T1", 3));
        assertEquals(new Position(3, 0), tank.getPosition());
    }

    @Test
    void testMoveTank_BlockedByObstacle() {
        Game game = new Game(42L, 20, 20);
        game.getBoard().setCell(0, 0, Board.EMPTY);
        game.getBoard().setCell(2, 0, Board.OBSTACLE);

        Tank tank = new LightTank("T1", new Position(0, 0), Direction.RIGHT);
        game.addTank(tank);

        assertThrows(IllegalArgumentException.class, () -> game.moveTank("T1", 3));
        assertEquals(new Position(0, 0), tank.getPosition()); // Танк не сдвинулся
    }

    @Test
    void testMoveTank_BlockedByAnotherTank() {
        Game game = new Game(42L, 20, 20);
        game.getBoard().setCell(0, 0, Board.EMPTY);
        game.getBoard().setCell(2, 0, Board.EMPTY);

        Tank tank1 = new LightTank("T1", new Position(0, 0), Direction.RIGHT);
        Tank tank2 = new MediumTank("T2", new Position(2, 0), Direction.LEFT);

        game.addTank(tank1);
        game.addTank(tank2);

        assertThrows(IllegalArgumentException.class, () -> game.moveTank("T1", 3));
        assertEquals(new Position(0, 0), tank1.getPosition());
    }

    @Test
    void testMoveTank_OutOfBounds() {
        Game game = new Game(42L, 20, 20);
        game.getBoard().setCell(18, 0, Board.EMPTY);

        Tank tank = new LightTank("T1", new Position(18, 0), Direction.RIGHT);
        game.addTank(tank);

        assertThrows(IllegalArgumentException.class, () -> game.moveTank("T1", 3));
        assertEquals(new Position(18, 0), tank.getPosition());
    }

    @Test
    void testMoveTank_ExceedsMobility() {
        Game game = new Game(42L, 20, 20);
        game.getBoard().setCell(0, 0, Board.EMPTY);

        Tank tank = new HeavyTank("T1", new Position(0, 0), Direction.RIGHT); // Подвижность 2
        game.addTank(tank);

        assertThrows(IllegalArgumentException.class, () -> game.moveTank("T1", 3));
        assertEquals(new Position(0, 0), tank.getPosition());
    }

    @Test
    void testLineOfSight_Clear() {
        Game game = new Game(42L, 20, 20);
        game.getBoard().setCell(0, 0, Board.EMPTY);
        game.getBoard().setCell(5, 0, Board.EMPTY);

        Tank shooter = new LightTank("T1", new Position(0, 0), Direction.RIGHT);
        Tank target = new MediumTank("T2", new Position(5, 0), Direction.LEFT);

        game.addTank(shooter);
        game.addTank(target);

        Tank result = game.checkLineOfSight(shooter);
        assertEquals(target, result);
    }

    @Test
    void testLineOfSight_BlockedByObstacle() {
        Game game = new Game(42L, 20, 20);
        game.getBoard().setCell(0, 0, Board.EMPTY);
        game.getBoard().setCell(3, 0, Board.OBSTACLE);
        game.getBoard().setCell(5, 0, Board.EMPTY);

        Tank shooter = new LightTank("T1", new Position(0, 0), Direction.RIGHT);
        Tank target = new MediumTank("T2", new Position(5, 0), Direction.LEFT);

        game.addTank(shooter);
        game.addTank(target);

        Tank result = game.checkLineOfSight(shooter);
        assertNull(result); // Препятствие блокирует
    }

    @Test
    void testShoot_HitAndDamage() {
        Game game = new Game(42L, 20, 20);
        game.getBoard().setCell(0, 0, Board.EMPTY);
        game.getBoard().setCell(5, 0, Board.EMPTY);

        Tank shooter = new HeavyTank("T1", new Position(0, 0), Direction.RIGHT); // Урон 50, пробитие 75
        Tank target = new LightTank("T2", new Position(5, 0), Direction.LEFT); // HP 100, лоб 30

        game.addTank(shooter);
        game.addTank(target);

        ShotResult result = game.shoot("T1");

        assertNotNull(result);
        assertTrue(result.penetrated());
        assertEquals(50, result.damage());
        assertEquals(50, result.hpAfterShot()); // 100 - 50
        assertFalse(result.destroyed());
    }

    @Test
    void testShoot_Miss() {
        Game game = new Game(42L, 20, 20);
        game.getBoard().setCell(0, 0, Board.EMPTY);

        Tank shooter = new LightTank("T1", new Position(0, 0), Direction.RIGHT);
        game.addTank(shooter);

        ShotResult result = game.shoot("T1");
        assertNull(result); // Нет цели на линии огня
    }
}