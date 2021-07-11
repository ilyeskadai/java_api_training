package fr.lernejo.navy_battle;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.*;
import java.net.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        Serv ser = null;
        try {
            ser = new Serv(args[0]);
            if (args.length > 1) {
                Client client = new Client(ser, args[1]);
                client.SeConnecterAuServ();
            }
        } catch (IOException | URISyntaxException e) {
            throw e;
        }
    }
}
