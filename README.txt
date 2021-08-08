Title of application: Schedule App


Purpose:
This is a scheduling application to interface with a MySQL database. It allows an authorized user to access the database in order to manage personal contacts and appointments. Upon login, the user is notified if there are any appointments occurring within 15 minutes. The user is then met with a home screen consisting of a TableView populated with appointments occurring within the next 24 hours. 

Interacting with the sidebar allows the user to create, read, update, and delete information from the Customers and Appointments tables. Per specification, the user may interact with tabs on the Appointments screen to view schedule by week, month, or all. The user may additionally generate reports to count appointments by type and month, list customers by country, or list appointments by contact. Per specification, all login attempts are recorded and appended to a login_activity.txt file.

The goal of this exercise was to demonstrate competencies in advanced Java programming language constructs, including lambda expressions, collections, localization, and exception control. A successful program adequately meets business requirements that might mirror a real-world job assignment. 


Author information:
Misty Hurley
mhurl10@wgu.edu
Application version 1.2
2021-05-12

Version 1.2 notes:
Altered appointment dialog box to display upcoming appointment date and time
Added human-readable UTC date and timestamps to 'login_activity.txt' to reduce confusion
Added validation check for overlapping appointments. Per spec, this check should only apply for customers, and appointments may start at the instant another appointment ends.

Version 1.1 notes:
Fixed error in time zone conversion logic, which incorrectly triggered business hours error when attempting to add an appointment
Fixed business hours error dialog to convert to local time instead of displaying EST (Please note that task requirements lists business hours as 8AM to 10PM Eastern Standard Time so business hours should not shift with Daylight Savings Time)
Fixed appointment cancelled confirmation and info boxes - added appointment type
Fixed a logical error involving timestamps and related conversions. 'login_activity.txt' should now list correct UTC timestamps for future login attempts. Alerts should now work correctly. System clock is used for appointment filtering and upcoming appointment notifications for evaluation purposes; was previously using MySQL server CURRENT_TIMESTAMP 


This application was designed using: 
IntelliJ IDEA Community Edition 2021.1
MySQL Connector v. 8.0.23 for JDBC
MySQL WorkBench 8.0 CE
openJFX javafx-sdk-11.0.2 compatible with JDK 11
SceneBuilder 16.0.0


Directions for running the program
1. Ensure that the appropriate MySQL connector and JDK versions are used.
2. Build and run Main.java
3. Login with username: test, password: test OR username: admin, password: admin to access the Home screen.
4. Interact with the sidebar to access the desired screen. 


Report choice: Customers by country.
1. Access this report by clicking the Reports button on the application sidebar. 2. Click the button labeled "Customers by Country"
3. In the top right corner of the screen, select a country from the drop down menu.
The TableView will be populated with a list of customer records located in the country of the user's choice.
4. Click Done in the bottom right corner to return to the Reports screen.
