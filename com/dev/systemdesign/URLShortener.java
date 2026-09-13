/*
Encode/decode long URLs to/from short codes with collision-safe storage, using a
counter-based base62 encoding scheme, common system design interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.HashMap;
import com.dev.logger.basePrinter;

class URLShortener extends basePrinter{

    private static final String BASE62_ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final HashMap<String, String> shortToLong;
    private final HashMap<String, String> longToShort;
    private long counter;
    private final String baseUrl;

    URLShortener(String baseUrl){
        this.shortToLong = new HashMap<>();
        this.longToShort = new HashMap<>();
        this.counter = 1; // start at 1 so the first code isn't just "0"
        this.baseUrl = baseUrl;
    }

    private String encodeBase62(long value){
        StringBuilder sb = new StringBuilder();
        if (value == 0){
            return "0";
        }
        while (value > 0){
            int remainder = (int) (value % 62);
            sb.append(BASE62_ALPHABET.charAt(remainder));
            value /= 62;
        }
        return sb.reverse().toString();
    }

    String shorten(String longUrl){
        // return existing short code if this url was already shortened, keeps storage collision-safe
        if (longToShort.containsKey(longUrl)){
            return baseUrl + "/" + longToShort.get(longUrl);
        }

        String code;
        do {
            code = encodeBase62(counter);
            counter++;
        } while (shortToLong.containsKey(code)); // defensive, counter-based codes never actually collide

        shortToLong.put(code, longUrl);
        longToShort.put(longUrl, code);
        return baseUrl + "/" + code;
    }

    String expand(String shortUrlOrCode){
        String code = shortUrlOrCode.contains("/")
                ? shortUrlOrCode.substring(shortUrlOrCode.lastIndexOf('/') + 1)
                : shortUrlOrCode;
        String longUrl = shortToLong.get(code);
        if (longUrl == null){
            throw new IllegalArgumentException("unknown short code: " + code);
        }
        return longUrl;
    }

    public static void main(String[] args) {
        URLShortener shortener = new URLShortener("https://short.ly");

        String short1 = shortener.shorten("https://example.com/very/long/path/one");
        String short2 = shortener.shorten("https://example.com/very/long/path/two");
        String short3 = shortener.shorten("https://example.com/very/long/path/one"); // duplicate input

        logp("short1 -> " + short1);
        logp("short2 -> " + short2);
        logp("short3 (same long url as short1) -> " + short3);
        logp("short1 and short3 identical (collision-safe on same input) -> " + short1.equals(short3));

        logp("expand(short1) -> " + shortener.expand(short1));
        logp("expand(short2) -> " + shortener.expand(short2));

        try {
            shortener.expand("https://short.ly/doesNotExist");
        } catch (IllegalArgumentException e) {
            logp("expected error expanding unknown code -> " + e.getMessage());
        }
    }
}
