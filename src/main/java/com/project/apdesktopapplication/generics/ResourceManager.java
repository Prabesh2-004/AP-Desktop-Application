package com.project.apdesktopapplication.generics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A reusable, type-safe, in-memory collection manager.
 * <p>
 * The bounded type parameter {@code <T extends Identifiable>} lets one single
 * class manage every entity in the system - users, resources and bookings -
 * while still guaranteeing at compile time that whatever is stored can be
 * looked up by its id. This is the DRY pay-off of generics: instead of writing
 * a near-identical UserStore, ResourceStore and BookingStore, the three service
 * classes each delegate to their own {@code ResourceManager<T>}.
 *
 * @param <T> any type that implements {@link Identifiable}
 */
public class ResourceManager<T extends Identifiable> {

    private final List<T> items = new ArrayList<>();

    /** Adds one item to the collection. */
    public void add(T item) {
        items.add(item);
    }

    /** Adds a whole batch. The {@code ? extends T} wildcard accepts any subtype list. */
    public void addAll(List<? extends T> batch) {
        items.addAll(batch);
    }

    /** Removes the item whose id matches; returns true if something was removed. */
    public boolean removeById(String id) {
        return items.removeIf(item -> item.getId().equals(id));
    }

    /** Replaces the stored item that shares the given item's id. */
    public boolean update(T updated) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(updated.getId())) {
                items.set(i, updated);
                return true;
            }
        }
        return false;
    }

    /** Finds an item by id, or null if none matches. */
    public T getById(String id) {
        return items.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /** Same lookup, expressed as an Optional for callers that prefer it. */
    public Optional<T> findById(String id) {
        return items.stream().filter(item -> item.getId().equals(id)).findFirst();
    }

    /** Returns a defensive copy of everything held. */
    public List<T> getAll() {
        return new ArrayList<>(items);
    }

    /**
     * Generic search: returns every item matching the supplied condition.
     * Callers pass a lambda, e.g. {@code findAll(r -> r.getStatus().equals("AVAILABLE"))},
     * so filtering by type, location, status or owner all reuse this one method.
     */
    public List<T> findAll(Predicate<T> condition) {
        return items.stream().filter(condition).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /** Counts items matching a condition. */
    public long count(Predicate<T> condition) {
        return items.stream().filter(condition).count();
    }

    /** Total number of items. */
    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }
}
