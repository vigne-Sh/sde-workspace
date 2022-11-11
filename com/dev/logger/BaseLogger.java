package src.com.dev.logger;

import org.apache.log4j.Logger;

class BaseLogger{
    private static final Logger log = Logger.getLogger("HelloWorld");
    public static void main(String[] args) {
        log.info("Hello, World!");
    }
}