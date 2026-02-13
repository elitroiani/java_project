package model;

import java.util.Objects;

/**
 * Configuration template for a specific type of ship.
 * Defines the ship's name, its size (number of cells), and how many 
 * instances should be placed on the grid.
 * * This is an IMMUTABLE configuration class, not a game entity.
 */
public final class ShipConfig {

    private final String name;
    private final int size;
    private final int count;

    /**
     * Creates a new ship configuration with validation.
     * @param name Display name of the ship.
     * @param size Length of the ship in cells.
     * @param count How many ships of this type exist in a standard fleet.
     * @throws IllegalArgumentException if parameters are invalid.
     */
    public ShipConfig(String name, int size, int count) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ship name cannot be null or blank");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Ship size must be positive");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Ship count must be positive");
        }

        this.name = name;
        this.size = size;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return name + " (size=" + size + ", count=" + count + ")";
    }

    /**
     * Value-based equality check. 
     * Two configurations are considered equal if all their fields match.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShipConfig)) return false;
        ShipConfig that = (ShipConfig) o;
        return size == that.size &&
               count == that.count &&
               name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size, count);
    }
}