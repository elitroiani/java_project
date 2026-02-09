package model;

import java.util.Objects;

/**
 * Configurazione di un tipo di nave:
 * - nome
 * - dimensione
 * - numero di navi da posizionare
 *
 * È una classe IMMUTABILE di configurazione (non una Ship di gioco).
 */
public final class ShipConfig {

    private final String name;
    private final int size;
    private final int count;

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
