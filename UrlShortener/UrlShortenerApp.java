

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class UrlShortenerManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, String> shortToLongMap;
    private Map<String, String> longToShortMap;

    public UrlShortenerManager() {
        this.shortToLongMap = new HashMap<>();
        this.longToShortMap = new HashMap<>();
    }

    public String shortenUrl(String longUrl) {
        if (longToShortMap.containsKey(longUrl)) {
            System.out.println("URL already shortened.");
            System.out.println("Shortened url is : "+longToShortMap.get(longUrl)) ;
            return "";
        }

        String shortUrl = generateShortUrl(longUrl);
        shortToLongMap.put(shortUrl, longUrl);
        longToShortMap.put(longUrl, shortUrl);

        System.out.println("Shortened URL: " + shortUrl);
        return shortUrl;
    }

    public String expandUrl(String shortUrl) {
        if (!shortToLongMap.containsKey(shortUrl)) {
            System.out.println("Invalid short URL.");
            return null;
        }

        String expandedUrl = shortToLongMap.get(shortUrl);
        System.out.println("Expanded URL: " + expandedUrl);
        return expandedUrl;
    }

    private String generateShortUrl(String longUrl) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(longUrl.getBytes());

            StringBuilder shortUrl = new StringBuilder();
            for (byte b : hashBytes) {
                shortUrl.append(String.format("%02x", b & 0xff));
            }

            return shortUrl.toString().substring(0, 8); // Use the first 8 characters as the short URL
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("urlShortener.ser"))) {
            oos.writeObject(this);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UrlShortenerManager loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("urlShortener.ser"))) {
            return (UrlShortenerManager) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new UrlShortenerManager();
        }
    }
}

public class UrlShortenerApp {
    public static void main(String[] args) {
        UrlShortenerManager urlShortenerManager = UrlShortenerManager.loadState();

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Shorten URL");
            System.out.println("2. Expand URL");
            System.out.println("3. Save and Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter the long URL to shorten: ");
                    String longUrl = scanner.nextLine();
                    urlShortenerManager.shortenUrl(longUrl);
                    break;
                case 2:
                    System.out.print("Enter the short URL to expand: ");
                    String shortUrl = scanner.nextLine();
                    urlShortenerManager.expandUrl(shortUrl);
                    break;
                case 3:
                    urlShortenerManager.saveState();
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
        scanner.close();
    }
}
