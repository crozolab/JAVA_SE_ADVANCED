package amazonviewer.intefaces;

import amazonviewer.model.Movie;

import java.sql.SQLException;

@FunctionalInterface
public interface MoviesDo<T> {
    void doSomething(T collection) throws SQLException;
}
