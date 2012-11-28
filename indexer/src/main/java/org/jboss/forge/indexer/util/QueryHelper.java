package org.jboss.forge.indexer.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.jandex.Indexer;
import org.jboss.jandex.JarIndexer;

public class QueryHelper
{

   static Connection connection;
   static Statement statement;
   static PreparedStatement preparedStatement;

   static String CLASS_CREATE_QUERY = "CREATE TABLE FORGE_CLASS (ID BIGINT IDENTITY, CREATED DATETIME, "
            + "JAR_NAME VARCHAR(200), CLASS_NAME VARCHAR(200), PACKAGE_NAME VARCHAR(200), PRIMARY KEY (ID))";
   static String CREATE_INDEX = "CREATE INDEX ON FORGE_CLASS (JAR_NAME, CLASS_NAME, PACKAGE_NAME)";
   static String INSERT_CLASS_QUERY = "INSERT INTO FORGE_CLASS (CREATED, JAR_NAME, CLASS_NAME, PACKAGE_NAME) VALUES (?,?,?,?)";
   static String DELETE_FROM_JAR_QUERY = "DELETE FROM FORGE_CLASS WHERE JAR_NAME=?";
   static String DELETE_QUERY = "DELETE FROM FORGE_CLASS WHERE JAR_NAME=?, CLASS_NAME=?, PACKAGE_NAME=?";
   static String SEARCH_JAR_QUERY = "SELECT COUNT(ID) FROM FORGE_CLASS WHERE JAR_NAME =?";
   static String SEARCH_QUERY = "SELECT ID, CREATED, JAR_NAME, CLASS_NAME, PACKAGE_NAME FROM FORGE_CLASS WHERE ";
   static String AND = " AND ";
   static String JAR_NAME_COND = " JAR_NAME = ?";
   static String CLASS_NAME_COND = " CLASS_NAME = ?";
   static String PACKAGE_NAME_COND = " PACKAGE_NAME = ?";

   private static int addToIndex(String jarName, int num, boolean forceUpdate)
            throws SQLException
   {
      File source = new File(jarName);
      Indexer indexer = new Indexer();
      String classN, packageN, jarN;
      List<String> result = new ArrayList<String>();
      if (jarName.contains("/"))
      {
         jarN = jarName.substring(jarName.lastIndexOf("/") + 1);
      }
      else
      {
         jarN = jarName;
      }
      if (!forceUpdate && existJarOnIndex(jarN))
      {
         return 0;
      }
      try
      {
         result = JarIndexer.createFlowerIndex(source, indexer);
         for (String className : result)
         {
            if (className.contains("$"))
            {
               System.out.println("SKIP: " + className);
               continue;
            }

            if (className.contains("."))
            {
               classN = className
                        .substring(className.lastIndexOf(".") + 1);
               packageN = className.substring(0,
                        className.lastIndexOf("."));
            }
            else
            {
               classN = className;
               packageN = "";
            }

            insert(jarN, classN, packageN);
            num++;
            System.out.println(num + ")" + classN + ": " + jarN + ":"
                     + packageN);
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      return num;
   }

   private static void insert(String jarName, String className,
            String packageName) throws SQLException
   {
      System.out.println(jarName + "," + className + "," + packageName);
      preparedStatement = connection.prepareStatement(INSERT_CLASS_QUERY);
      preparedStatement.setTimestamp(1,
               new Timestamp(System.currentTimeMillis()));
      preparedStatement.setString(2, jarName);
      preparedStatement.setString(3, className);
      preparedStatement.setString(4, packageName);
      int result = preparedStatement.executeUpdate();
   }

   private static boolean insert(String jarName, String className)
            throws SQLException
   {
      String packageName = className.substring(0, className.lastIndexOf("."));
      preparedStatement = connection.prepareStatement(INSERT_CLASS_QUERY);
      preparedStatement.setTimestamp(1,
               new Timestamp(System.currentTimeMillis()));
      preparedStatement.setString(2, jarName);
      preparedStatement.setString(3, className);
      preparedStatement.setString(4, packageName);
      System.out.println(jarName + "," + className + "," + packageName);
      int result = preparedStatement.executeUpdate();
      return true;
   }

   private static int insert(List<String> className, String jarName)
            throws SQLException
   {
      int num = 0;
      for (String classN : className)
      {
         String packageName = classN.substring(0, classN.lastIndexOf("."));
         preparedStatement = connection.prepareStatement(INSERT_CLASS_QUERY);
         preparedStatement.setTimestamp(1,
                  new Timestamp(System.currentTimeMillis()));
         preparedStatement.setString(2, jarName);
         preparedStatement.setString(3, classN);
         preparedStatement.setString(4, packageName);
         int result = preparedStatement.executeUpdate();
         num++;
      }
      return num;
   }

   private static int delete(String jarName) throws SQLException
   {
      preparedStatement = connection.prepareStatement(DELETE_FROM_JAR_QUERY);
      preparedStatement.setString(1, jarName);
      int result = preparedStatement.executeUpdate();
      return result;
   }

   private static void delete(String jarName, String className,
            String packageName) throws SQLException
   {
      preparedStatement = connection.prepareStatement(DELETE_QUERY);
      preparedStatement.setString(1, jarName);
      preparedStatement.setString(2, className);
      preparedStatement.setString(3, packageName);
      int result = preparedStatement.executeUpdate();
   }

   private static List<ForgeClass> find(String jarName, String className,
            String packageName) throws SQLException
   {
      List<ForgeClass> results = new ArrayList<ForgeClass>();
      StringBuffer queryBuffer = new StringBuffer();
      if (jarName != null && !jarName.trim().isEmpty())
      {
         queryBuffer.append(AND).append(JAR_NAME_COND);
      }
      if (className != null && !className.trim().isEmpty())
      {
         queryBuffer.append(AND).append(CLASS_NAME_COND);
      }
      if (packageName != null && !packageName.trim().isEmpty())
      {
         queryBuffer.append(AND).append(PACKAGE_NAME_COND);
      }
      String query = SEARCH_QUERY.concat(queryBuffer.toString().substring(
               AND.length() + 1));
      preparedStatement = connection.prepareStatement(query);
      int i = 0;
      if (jarName != null && !jarName.trim().isEmpty())
      {
         preparedStatement.setString(++i, jarName);
      }
      if (className != null && !className.trim().isEmpty())
      {
         preparedStatement.setString(++i, className);
      }
      if (packageName != null && !packageName.trim().isEmpty())
      {
         preparedStatement.setString(++i, packageName);
      }

      try
      {
         ResultSet rs = preparedStatement.executeQuery();
         while (rs.next())
         {
            // ID, CREATED, JAR_NAME, CLASS_NAME, PACKAGE_NAME
            Long idN = rs.getLong(1);
            Date createdN = rs.getTimestamp(2);
            String jarN = rs.getString(3);
            String classN = rs.getString(4);
            String packN = rs.getString(5);

            // print query result to console
            System.out.println("id: " + idN + " .createdN: " + createdN
                     + " .jarN: " + jarN + " .packN: " + packN
                     + " .classN: " + classN);
            results.add(new ForgeClass(idN, classN, packN, jarN));
         }
         rs.close();

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }

      return results;
   }

   private static boolean existJarOnIndex(String jarName) throws SQLException
   {
      preparedStatement = connection
               .prepareStatement(SEARCH_JAR_QUERY);

      preparedStatement.setString(1, jarName);
      ResultSet rs = preparedStatement.executeQuery();
      try
      {
         while (rs.next())
         {
            // ID, CREATED, JAR_NAME, CLASS_NAME, PACKAGE_NAME
            Long num = rs.getLong(1);

            System.out
                     .println("NUM ROWS WITH JAR:" + jarName + " - " + num);
            if (num != null && num > 0)
               return true;
         }

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      finally
      {
         rs.close();
      }

      return false;
   }

   private static void startDb() throws ClassNotFoundException, SQLException
   {
      String dbName = "DB_FLOWER";
      // + System.currentTimeMillis();

      Class.forName("org.h2.Driver");
      String url = "jdbc:h2:/tmp/" + dbName + ";AUTO_SERVER=TRUE";
      String user = "sa";
      String pwd = "";

      // Server server = Server.createTcpServer(url, user, pwd).start();
      connection = DriverManager.getConnection(url, user, pwd);
      statement = connection.createStatement();
      try
      {
         statement.execute(CLASS_CREATE_QUERY);
         statement.execute(CREATE_INDEX);
      }
      catch (Exception e)
      {
         System.out.println(e.getMessage());
      }

   }

   private static void stopDb() throws SQLException
   {
      connection.close();
   }
}
