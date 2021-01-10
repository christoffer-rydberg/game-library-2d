package com.gamelibrary2d.common.functional;

public class Functions {

    public static <T> T Id(T t) {
        return t;
    }

    public static <T> Void Ignore(T t) {
        return null;
    }

    /**
     * Performs a mutating action and then returns the specified object.
     *
     * @param action The action to perform.
     * @param obj    The instance to return.
     * @return The instance parameter.
     */
    public static <T> T PerformAndReturn(Action action, T obj) {
        action.perform();
        return obj;
    }

    /**
     * Performs a mutating action and then creates an object to return.
     *
     * @param action     The action to perform.
     * @param objFactory Creates the instance to return.
     * @return The instance created by the instance factory.
     */
    public static <T> T PerformAndCreate(Action action, Factory<T> objFactory) {
        action.perform();
        return objFactory.create();
    }
}
