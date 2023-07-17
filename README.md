Final project: Client-Server application done without additional frameworks.
How to run ?
In IntelliJIDEA for ClientChat application => go to "Edit Configurations..." => then click "Modify options" => and enable "Allow multiple instances".

Chat functionalities:
1. Chat allows give chosen user name for chat client user.
2. Chat allows to send messages to all users: you write your message and every chat user receives this message.
3. Chat allows to send private messages. 

a) If you have three users: Mike, Fred and Joe:
- if you start your message from @ and then existing user name:
e.g. if Mike starts message like that:		@Fred How are you ?
then only Fred receives message like that:
Mike PRIVATE> How are you ?

b) if Mike starts message like that with non-existing user name: @Dan How are you ?
then only Mike see such message:
Dan probably is wrong or non-existing user name!!!

What I have learned during working with this application:

I have learned:
1. How to enable experimental features in Java (I have used Virtual Threads from Project Loom in this project).
2. How to create POM with modules and create three different applications: parent chat and two child module apllications chat-client and chat-server.
3. How to use try-catch construction and when finally is not a good choice (I had some bugs connected with this).
4. How to use Java Streams object like BufferedReader and BufferedWriter - how to set up and use them properly.
5. How to use inner classes.
6. How to use threads in client-server application.
7. How to use ServerSocket and Socket classes - programming with Sockets.
8. How to create multi-threaded application with mix of traditional threads with new Virtual Threads - project Loom.
9. How to use new Java features like class inference with var for local variables.
10. How to use Java thread safe collections CopyOnWriteArrayList which is a thread-safe variant of ArrayList and assure thread safety with synchronized keyword.