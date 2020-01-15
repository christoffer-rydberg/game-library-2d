package com.gamelibrary2d.objects;

/**
 * Defines a modifiable {@link Container} where {@link GameObject game objects}
 * and be added and removed).
 *
 * @author Christoffer Rydberg
 */
public interface ModifiableContainer<T extends GameObject> extends Clearable, Container<T> {

    GameObject get(int index);

    void add(T obj);

    void add(int index, T obj);

    void remove(int index);

    boolean remove(T obj);

}