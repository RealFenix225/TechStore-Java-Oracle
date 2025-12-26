package com.techstore.exception;

public class StockInsufficientException extends Exception {
    //Esto servirá exclusivamente para cuando alguien quiera comprar más de lo que hay
    public StockInsufficientException(String message) {
        super(message);
    }
}
