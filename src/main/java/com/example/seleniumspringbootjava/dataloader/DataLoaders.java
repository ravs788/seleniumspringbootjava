package com.example.seleniumspringbootjava.dataloader;

/**
 * Convenience accessors for {@link DataLoader} implementations.
 *
 * <p>
 * Centralizing access here allows us to add new formats (CSV, Excel, DAT, etc.)
 * without changing test code beyond selecting a different loader.
 * </p>
 */
public final class DataLoaders {

    private static final DataLoader JSON = new JsonDataLoader();

    private DataLoaders() {
    }

    public static DataLoader json() {
        return JSON;
    }
}
