package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Serv {


    final int port;
    final String url;
    final HttpServer ser;
    final String serverID;
    final Request rqtHdlr;
    final Game Game;
    final String[] target;

    void GenererHTMLmsg(HttpExchange exchange, int error) throws IOException {
        String body = String.format("ERROR 403 (srry)", error);
        exchange.sendResponseHeaders(error, body.length());
        try (OutputStream os = exchange.getResponseBody()) {os.write(body.getBytes());}
    }

    final HttpHandler PingRespond = new HttpHandler() {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String body = String.format("OK");
            exchange.sendResponseHeaders(200, body.length());
            try (OutputStream os = exchange.getResponseBody()) {os.write(body.getBytes());}
        }
    };

    final HttpHandler FireRespond = new HttpHandler() {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("GET")) {
                GenererHTMLmsg(exchange, 404);
                return;
            }
            rqtHdlr.Fire(exchange,false);
        }
    };

    final HttpHandler StartRespond = new HttpHandler() {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (Game.inGame[0] || !exchange.getRequestMethod().equals("POST")) {
                GenererHTMLmsg(exchange, 404);
                return;
            }
            try {rqtHdlr.StartHandler(exchange,false);} catch (InterruptedException e) {e.printStackTrace();}
        }
    };

    final HttpHandler DefaultRespond = new HttpHandler() {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            GenererHTMLmsg(exchange, 404);
        }
    };

    Serv(String port) throws IOException {
        this.port = Integer.parseInt(port);
        url = String.format("http://localhost:%s",port);
        ser = HttpServer.create(new InetSocketAddress(this.port),50);
        ser.setExecutor(Executors.newSingleThreadExecutor());
        ser.createContext("/ping",this.PingRespond);
        ser.createContext("/api/game/start",this.StartRespond);
        ser.createContext("/api/game/fire",this.FireRespond);
        ser.createContext("/",this.DefaultRespond);
        this.serverID = UUID.randomUUID().toString();
        rqtHdlr = new Request(this);
        ser.start();
        Game = new Game(this);
        target = new String[]{""};
    }
}
