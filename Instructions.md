Requirements

⦁	Java 25

⦁	Maven (for building the backend)

⦁	MySQL (for the database)

⦁	A web server like Apache or Nginx (for serving the static website), or use Python's built-in server



Step 1: Set Up the Database

1\.	Open a command prompt or terminal and navigate to the PREMI-CCIS directory (where room\_match\_master.sql is located).

2\.	Run the SQL script using the MySQL command-line tool: mysql -u root -p < room\_match\_master.sql

⦁	Replace root with your MySQL username if different.

⦁	You'll be prompted for the password (use the password for your MySQL user, e.g., 122105keno! if that's your root password). (change the password too in resources -> application.properties)

After that run this commands: 

mysql -u root -p



SHOW TABLES;

SELECT \* FROM users;

So that you can see the logins you can use.



Step 2: Run the Java Console Application

1\.	Change the password in (DBConnection.java) into your mysql password 

2\.	Open another CMD 

3\.	Run this command:  

java -cp "src;mysql-connector-j-9.3.0/mysql-connector-j-9.3.0/mysql-connector-j-9.3.0.jar" main.Main

4\. Type in the user and password that was shown in the sql.  (You can now access the application via CMD)



Step 3: Run the Spring Boot Backend

1\.	Open CMD (You can exit the other two if you want)

2\.	Run this command: 

cd PREMI-CCIS/backend

.\\mvnw.cmd spring-boot:run

WARNING (DO NOT CLOSE THE CMD WHILE RUNNING MVN)



Step 4: Running the Website Application

1\.	Open CMD and run this command: 

cd C:\\PROJECT\\PREMI-CCIS\\PREMI-CCIS\\website

python -m http.server 8000

WARNING (DO NOT CLOSE THE CMD WHILE RUNNING)

2\. Access the website:

Main page: http://localhost:8000/html/index.html

3\. And you’re done. 





