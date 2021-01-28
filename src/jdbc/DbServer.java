package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DbServer implements IDbServer {
    public static final String url = "jdbc:mysql://localhost:3306/130_1";
    public static final String user = "root";
    public static final String pwd = "root";
    private static DbServer instance = null;
    private static String QUERY;
    private PreparedStatement pst;

    public static void main(String[] args) throws SQLException, DocumentException {
        try (DbServer server = new DbServer()) {
            server.checkDriver();
            server.getAllAuthors();
            Author author1 = new Author(22, "Arnold Grey", "Во и Ми");
            Author author2 = new Author(11, "Лев Толстой", "Война и Мир");
            Author author3 = new Author(12, "Тотпа Лора", "Война");
            Author author4 = new Author(13, "Тоа Маце", "Вой");
            Author author5 = new Author(3, "То це", "Вий");
            Document document1 = new Document(20, "Рамо", "Text and text", 22);
            Document document2 = new Document(10, "Рамкуо", "Text and text", 3);
            Document document3 = new Document(11, "Рамкуо", "Text and text", 1);

            server.addAuthor(author1);
//        server.addAuthor(author4);
//        server.addAuthor(author3);


//        server.addDocument(document1, author1);
//        server.addDocument(document1, author4);
//        server.addDocument(document2, author5);

            server.findDocumentByAuthor(author1);
//        server.findDocumentByContent("%text%");

//        server.deleteAuthor(22);

            server.close();
        }

    }


    public static synchronized DbServer getInstance() throws SQLException {
        if (instance == null)
            instance = new DbServer();
        return instance;
    }

    // Объект, в котором будет храниться соединение с БД
    private Connection connection;

    private DbServer() throws SQLException {
        // Регистрируем драйвер, с которым будем работать
        // в нашем случае Sqlite
//        Driver d;
//        d = new com.mysql.jdbc.Driver();
//        DriverManager.registerDriver(d);
        // Выполняем подключение к базе данных
        this.connection = DriverManager.getConnection(url, user, pwd);
        //Setting the auto commit false
        connection.setAutoCommit(false);
        //Setting the holdability to CLOSE_CURSORS_AT_COMMIT
        connection.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);

        System.out.println("Connection is already");
    }


    private void checkDriver() throws SQLException {
        Enumeration<Driver> e = DriverManager.getDrivers();
        while (e.hasMoreElements()) {
            Driver d = e.nextElement();
            System.out.println(d.getClass().getCanonicalName());


        }
        Driver d = DriverManager.getDriver(url);
        if (d != null) {
            System.out.println("Driver is OK");
        }
    }


    public List<Author> getAllAuthors() throws SQLException {

        try (Statement statement = this.connection.createStatement()) {
            List<Author> author = new ArrayList<Author>();
            try (ResultSet resultSet = statement.executeQuery("SELECT id, author_name, description FROM author")) {
                while (resultSet.next()) {
                    author.add(new Author(resultSet.getInt("id"),
                            resultSet.getString("author_name"),
                            resultSet.getString("description")));
                    System.out.println("My beatiful author is: " + author);

                }
            }
            return author;

        } catch (SQLException e) {
            System.out.println("Error" + e.getMessage());
            return Collections.emptyList();
        }

    }


    @Override
    public void addAuthor(Author author) throws DocumentException {
        try {
            PreparedStatement statement = this.connection.prepareStatement
                    ("SELECT COUNT(*) FROM Author where id = ?");
            statement.setInt(1, author.getAuthor_id());
            int count;
            try (ResultSet rs = statement.executeQuery()) {
                rs.next(); //перевод курсора на 1ю запись
                count = rs.getInt(1);
            }
            if (count > 0) {
                statement = this.connection.prepareStatement(
                QUERY = "UPDATE Author SET description = ?, author_name = ? WHERE id = ?;");

                statement.setString(1, author.getDescription());
                statement.setString(2, author.getAuthor());
                statement.setInt(3, author.getAuthor_id());
            }
                else{
                    statement = this.connection.prepareStatement(
                    QUERY = "INSERT INTO Author (id, author_name, description) VALUES (?, ?, ?);");

                statement.setInt(1, author.getAuthor_id());
                statement.setString(2, author.getAuthor());
                statement.setString(3, author.getDescription());
                }
                statement.execute();
                System.out.println("My beatiful author's documents has been added/updated here: " + author);
        } catch (SQLException e) {
            throw new DocumentException(e.getMessage());
        }
    }



    @Override
    public void deleteAuthor(int id) throws DocumentException {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM Author WHERE id = ?")) {
            statement.setInt(1, id);
            statement.execute();
            System.out.println("My beatiful author has been deleted here: " + id);
        } catch (SQLException e) {
            throw new DocumentException(e.getMessage());
        }
    }


    @Override
    public void deleteAuthor(Author author) throws DocumentException {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM Author WHERE author_name = ?")) {
            statement.setString(1, author.getAuthor());
            statement.execute();
            System.out.println("My beatiful author has been deleted here: " + author);
        } catch (SQLException e) {
            throw new DocumentException(e.getMessage());
        }
    }



    @Override
    public boolean addDocument(Document doc, Author author) throws DocumentException {
        try {
            PreparedStatement statement = this.connection.prepareStatement
                    ("SELECT COUNT(*) FROM Document where idDoc = ?");
            statement.setInt(1, doc.getDocument_id());
            int count;
            try (ResultSet rs = statement.executeQuery()) {
                rs.next(); //перевод курсора на 1ю запись
                count = rs.getInt(1);
            }
            if (count > 0) {
                statement = this.connection.prepareStatement
                        ("UPDATE Document SET docname = ?, textinfo = ?, createdate = ? where idDoc = ?");
                statement.setObject(1, doc.getTitle());
                statement.setObject(2, doc.getText());
                statement.setObject(3, doc.getDate());
                statement.setObject(4, doc.getDocument_id());

                statement.execute();
                System.out.println("My beatiful author's documents has been added here: " + doc);
                return false;
            } else {
                statement = this.connection.prepareStatement
                        ("INSERT INTO Document (idDoc, createdate, docname, linkId, textinfo) VALUES (?, ?, ?, ?, ?)");
                statement.setObject(1, doc.getDocument_id());
                statement.setObject(2, doc.getDate());
                statement.setObject(3, doc.getTitle());
                statement.setObject(4, doc.getAuthor_id());
                statement.setObject(5, doc.getText());

                statement.execute();
                System.out.println("My beatiful author's documents has been updated here: " + doc);
                return true;
            }
        } catch (SQLException e) {
            throw new DocumentException(e.getMessage());
        }
    }

    @Override
    public Document[] findDocumentByAuthor(Author author) throws DocumentException {
        try {
            PreparedStatement statement = this.connection.prepareStatement
                    ("SELECT idDoc, docname, textinfo, linkId FROM Document where linkId in (SELECT id from Author where author_name = ?)");
            statement.setString(1, author.getAuthor());
            ArrayList<Document> documents = new ArrayList<Document>();
            try (ResultSet rs = statement.executeQuery()) {
                System.out.println("My beatiful author's document: " + rs);
                while (rs.next()) {
                    Document document = new Document(
                            rs.getInt("idDoc"),
                            rs.getString("docname"),
                            rs.getString("textinfo"),
                            rs.getInt("linkId")
                    );
                    documents.add(document);
                }
            }
            documents.forEach(document -> System.out.println(document.toString()));
            return documents.toArray(new Document[0]);

        } catch (SQLException e) {
            throw new DocumentException(e.getMessage());
        }
    }


    @Override
    public Document[] findDocumentByContent(String content) throws DocumentException {
        try {
            PreparedStatement statement = this.connection.prepareStatement
                    ("SELECT idDoc, docname, textinfo, linkId FROM Document where textinfo like ?");
            statement.setObject(1, content);

            ArrayList<Document> documents = new ArrayList<Document>();
            try (ResultSet rs = statement.executeQuery()) {
                System.out.println("My beatiful author's content: " + rs);
                while (rs.next()) {
                    Document document = new Document(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getInt(4)
                    ) {
                    };
                    documents.add(document);

                }
            }
            documents.forEach(document -> System.out.println(document.toString()));
            return documents.toArray(new Document[0]);
        } catch (SQLException e) {
            throw new DocumentException(e.getMessage());
        }
    }




    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection is closed");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
