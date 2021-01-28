package jdbc;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Создаем экземпляр по работе с БД
            DbServer dbServer = DbServer.getInstance();
            // Добавляем запись
            //dbHandler.addAuthor(new Author("11", Hellisson Adams, "Huhlomuki and other adventures"));
            List<Author> authors = dbServer.getAllAuthors();
            for (Author author : authors) {
                System.out.println(author.toString());
            }
            // Удаление записи с id = 11
            //dbHandler.deleteProduct(11);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}