package com.github.kolandroid.kol.model.elements.basic;

import com.github.kolandroid.kol.model.elements.interfaces.ModelGroup;

import java.util.ArrayList;
import java.util.Iterator;

public class BasicGroup<E> implements ModelGroup<E> {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = 356357357356695L;

    private final ArrayList<E> items;
    private final String name;

    public BasicGroup(String name) {
        this(name, new ArrayList<E>());
    }

    public BasicGroup(String name, ArrayList<E> items) {
        this.name = name;
        this.items = items;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public E get(int index) {
        return items.get(index);
    }

    @Override
    public void set(int index, E value) {
        items.set(index, value);
    }

    @Override
    public void remove(int index) {
        items.remove(index);
    }

    public void add(E item) {
        items.add(item);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Iterator<E> iterator() {
        return items.iterator();
    }
}
