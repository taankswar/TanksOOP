package com.tanks.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TankTest {

    @Test
    void testFrontalHit_NoPenetration() {
        // Тяжелый танк стреляет в лоб среднего танка
        // Пробитие 75, Лобовая броня 50 -> 75 > 50, ДОЛЖЕН ПРОБИТЬ.
        // Исправим тест: пусть стреляет легкий (пробитие 40) в лоб среднего (броня 50).
        Tank shooter = new LightTank("light", new Position(0,0), Direction.RIGHT);
        Tank target = new MediumTank("medium", new Position(5,5), Direction.RIGHT);

        int damage = target.receiveShot(shooter.getStats().gunPenetration(), shooter.getStats().gunDamage(), ArmorZone.FRONT);

        assertEquals(0, damage); // Не пробил
        assertEquals(140, target.getCurrentHp()); // HP не изменился
    }

    @Test
    void testRearHit_Penetration() {
        // Легкий танк стреляет в корму среднего танка
        // Пробитие 40, Кормовая броня 20 -> 40 > 20, ПРОБИВАЕТ.
        Tank shooter = new LightTank("light", new Position(0,0), Direction.RIGHT);
        Tank target = new MediumTank("medium", new Position(5,5), Direction.LEFT); // Смотрит влево, корма справа

        int damage = target.receiveShot(shooter.getStats().gunPenetration(), shooter.getStats().gunDamage(), ArmorZone.REAR);

        assertEquals(25, damage); // Урон прошел
        assertEquals(115, target.getCurrentHp()); // 140 - 25
    }

    @Test
    void testArmorEqualsPenetration_NoDamage() {
        // Броня равна пробитию -> урон не проходит (согласно условию: если броня >= пробитие)
        Tank target = new MediumTank("medium", new Position(5,5), Direction.RIGHT);

        int damage = target.receiveShot(50, 35, ArmorZone.FRONT); // 50 == 50

        assertEquals(0, damage);
        assertEquals(140, target.getCurrentHp());
    }

    @Test
    void testTankDestruction() {
        Tank target = new LightTank("light", new Position(5,5), Direction.RIGHT);

        // Наносим урон, превышающий HP
        target.receiveShot(100, 200, ArmorZone.REAR);

        assertTrue(target.isDestroyed());
        assertEquals(0, target.getCurrentHp());
    }

    @Test
    void testCannotShootWhenDestroyed() {
        Tank target = new LightTank("light", new Position(5,5), Direction.RIGHT);
        target.receiveShot(100, 200, ArmorZone.REAR); // Уничтожаем

        assertThrows(IllegalStateException.class, () ->
                target.receiveShot(40, 25, ArmorZone.FRONT)
        );
    }

    @Test
    void testSpendAmmo() {
        Tank tank = new LightTank("light", new Position(5,5), Direction.RIGHT);
        assertEquals(12, tank.getCurrentAmmo());

        tank.spendAmmo();
        assertEquals(11, tank.getCurrentAmmo());
    }

    @Test
    void testUpdateState() {
        Tank tank = new LightTank("light", new Position(5,5), Direction.RIGHT);

        Position newPos = new Position(8, 5);
        tank.updateState(newPos, Direction.UP);

        assertEquals(newPos, tank.getPosition());
        assertEquals(Direction.UP, tank.getFacing());
    }
}