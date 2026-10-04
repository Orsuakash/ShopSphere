package com.ShopSphere.ExceptionHandling;

public class ResourceNotFoundExpection extends RuntimeException{
    public ResourceNotFoundExpection(String message){
        super(message);
    }
}
