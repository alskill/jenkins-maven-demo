import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class App {

    public static void main(String[] args) throws Exception {

        int port = 8080;

        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Jenkins Maven Demo Application started");
        System.out.println("Application running on port " + port);

        while (true) {

            Socket socket = serverSocket.accept();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            PrintWriter writer = new PrintWriter(
                    socket.getOutputStream()
            );

            reader.readLine();

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Jenkins Maven Demo</title>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                text-align: center;
                                margin-top: 100px;
                            }
                            h1 {
                                font-size: 42px;
                            }
                            .box {
                                width: 600px;
                                margin: auto;
                                padding: 30px;
                                border: 1px solid #ddd;
                                border-radius: 10px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="box">
                            <h1>Hello Jenkins CI/CD!</h1>
                            <h2>Maven Application Deployment Successful</h2>
                            <p>Application deployed successfully to Docker Desktop.</p>
                            <p>Build → Test → Deploy → Run</p>
                        </div>
                    </body>
                    </html>
                    """;

            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html; charset=UTF-8");
            writer.println(
                    "Content-Length: " +
                    html.getBytes(StandardCharsets.UTF_8).length
            );
            writer.println();
            writer.println(html);
            writer.flush();

            socket.close();
        }
    }
}