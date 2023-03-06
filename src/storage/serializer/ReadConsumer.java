package storage.serializer;

import model.Resume;

import java.io.IOException;

@FunctionalInterface
public interface ReadConsumer<T> {

    void read(T t) throws IOException;
}
