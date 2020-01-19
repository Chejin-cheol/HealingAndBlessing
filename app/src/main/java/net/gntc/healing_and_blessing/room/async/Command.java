package net.gntc.healing_and_blessing.room.async;

public interface Command<T> {
    void execute(T t);
}
