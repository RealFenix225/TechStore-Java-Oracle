package com.techstore.exception;

public class TechStoreException extends Exception {
    //Esto servirá para cualquier error general de la tienda.
    public TechStoreException(String message) {
        super(message);
    }
}
