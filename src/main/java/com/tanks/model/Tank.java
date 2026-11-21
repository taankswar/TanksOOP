package com.tanks.model;

/**
 * Базовый класс танка. Реализует полиморфизм техники.
 * Хранит текущее состояние танка (HP, позиция, направление).
 */
public abstract class Tank {

    private final String id;
    private final TankStats stats;

    private Position position;
    private Direction facing;
    private int currentHp;
    private int currentAmmo;

    protected Tank(String id, Position position, Direction facing, TankStats stats) {
        this.id = id;
        this.position = position;
        this.facing = facing;
        this.stats = stats;
        this.currentHp = stats.maxHp();
        this.currentAmmo = stats.maxAmmo();
    }

    // --- Геттеры ---

    public String getId() { return id; }
    public TankStats getStats() { return stats; }
    public Position getPosition() { return position; }
    public Direction getFacing() { return facing; }
    public int getCurrentHp() { return currentHp; }
    public int getCurrentAmmo() { return currentAmmo; }

    public boolean isDestroyed() {
        return currentHp <= 0;
    }

    public boolean canShoot() {
        return !isDestroyed() && currentAmmo > 0;
    }

    // --- Логика боя ---

    /**
     * Основной метод механики: получение урона.
     * @param penetration пробитие орудия стреляющего
     * @param damage урон орудия стреляющего
     * @param hitZone зона, в которую попали
     * @return фактический нанесенный урон (0, если не пробил)
     */
    public int receiveShot(int penetration, int damage, ArmorZone hitZone, double armorModifier) {
        if (isDestroyed()) {
            throw new IllegalStateException("Нельзя стрелять по уничтоженному танку");
        }

        int armor = (int)(stats.getArmor(hitZone) * armorModifier); // Усиление брони

        if (armor >= penetration) {
            return 0;
        }

        currentHp = Math.max(0, currentHp - damage);
        return damage;
    }

    /**
     * Обновление позиции и направления после хода.
     * (Проверку препятствий делает GameEngine, здесь только изменение состояния).
     */
    public void updateState(Position newPosition, Direction newFacing) {
        if (isDestroyed()) {
            throw new IllegalStateException("Уничтоженный танк не может двигаться");
        }
        this.position = newPosition;
        this.facing = newFacing;
    }

    /**
     * Расходует один снаряд.
     */
    public void spendAmmo() {
        if (currentAmmo <= 0) {
            throw new IllegalStateException("Нет боезапаса");
        }
        currentAmmo--;
    }

    /**
     * Проверяет, может ли танк пройти указанное количество клеток.
     * @param cells количество клеток
     * @return true, если танк может пройти
     */
    public boolean canMove(int cells) {
        return !isDestroyed() && cells > 0 && cells <= getStats().mobility();
    }

    /**
     * Проверяет, может ли танк повернуться в указанном направлении.
     * @param newDirection новое направление
     * @return true, если танк может повернуться
     */
    public boolean canRotate(Direction newDirection) {
        return !isDestroyed() && newDirection != null;
    }

    /**
     * Поворачивает танк в указанном направлении.
     * @param newDirection новое направление
     */
    public void rotate(Direction newDirection) {
        if (!canRotate(newDirection)) {
            throw new IllegalStateException("Танк не может повернуться");
        }
        this.facing = newDirection;
    }
}