package amazonviewer.dao;

import amazonviewer.db.IDBConnection;
import amazonviewer.model.Movie;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static amazonviewer.db.DataBase.*;

public interface MovieDAO extends IDBConnection {
    default void setMovieViewed(Movie movie) {

        try (Connection connection = connectToDB()) {
            Statement statement = connection.createStatement();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String query = "INSERT INTO " + TVIEWED +
                    "(" + TID_MATERIAL_NAME + "," + TID_ELEMENT_NAME + "," + TID_USER_NAME + "," + TID_DATE +")" +
                    " VALUES (" + TMATERIAL_ID[0] + "," + movie.getId() + "," + TUSER_IDUSUARIO +  "," + "'" + dateFormat.format(new Date()) +"'" +")";
            if (statement.executeUpdate(query) > 0) {
                System.out.println("se agrego exitosamente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    default List<Movie> read() {
        List<Movie> movies = new ArrayList<>();
        try (Connection connection = connectToDB()) {
            String query = "SELECT * FROM " + TMOVIE;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getString(TMOVIE_TITLE),
                        rs.getString(TMOVIE_GENRE),
                        rs.getString(TMOVIE_CREATOR),
                        rs.getInt(TMOVIE_DURATION),
                        rs.getShort(TMOVIE_YEAR));
                movie.setId(rs.getInt(TMOVIE_ID));
                movie.setViewed(getMovieViewed(connection, movie.getId()));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    private boolean getMovieViewed(Connection connection, int idMovie) {
        boolean viewed = false;
        String query = "SELECT * FROM " + TVIEWED +
                " WHERE " + TID_MATERIAL_NAME + "= ?" +
                " AND " + TID_ELEMENT_NAME + "= ? " +
                " AND " + TID_USER_NAME + "= ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, TMATERIAL_ID[0]);
            preparedStatement.setInt(2, idMovie);
            preparedStatement.setInt(3, TUSER_IDUSUARIO);
            ResultSet resultSet = preparedStatement.executeQuery();
            viewed = resultSet.next();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return viewed;
    }

    default List<Movie> getMoviesByDate(){
        String date = "2022-04-11 10:36:31";
        String query = "SELECT * FROM " + TVIEWED +
                " INNER JOIN " + TMOVIE + " ON "+ TMOVIE+".id" + "=" + TVIEWED+".id_element" +
                " WHERE " + TID_MATERIAL_NAME + "= ?" +
                " AND " + TID_USER_NAME + "= ?" +
                " AND " + TID_DATE + "= ?";
        List<Movie> moviesDate = new ArrayList<>();
        try (Connection connection = connectToDB()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, TMATERIAL_ID[0]);
            preparedStatement.setInt(2, TUSER_IDUSUARIO);
            preparedStatement.setString(3,  date);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getString(TMOVIE_TITLE),
                        rs.getString(TMOVIE_GENRE),
                        rs.getString(TMOVIE_CREATOR),
                        rs.getInt(TMOVIE_DURATION),
                        rs.getShort(TMOVIE_YEAR));
                movie.setId(rs.getInt(TMOVIE_ID));
                movie.setViewed(getMovieViewed(connection, movie.getId()));
                moviesDate.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  moviesDate;
    }

}
