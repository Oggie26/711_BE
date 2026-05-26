package com.example._1.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    //11xx
    INVALID_KEY(1100, "Invalid uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_JSON(1101, "Json invalid", HttpStatus.BAD_REQUEST),
    INVALID_LOGIN(1105, "Invalid Login", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1106, "Invalid Password", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(1118, "Invalid Status", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(1119, "Invalid Request", HttpStatus.BAD_REQUEST),
    EXTERNAL_SERVICE_ERROR(1120, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_DELETED(1121, "User Deleted", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1122, "Invalid Token", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(1123, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED(1124, "Access Denied", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(1125, "Unauthorized", HttpStatus.UNAUTHORIZED),
    //12xx
    UNAUTHENTICATED(1201, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    NOT_FOUND_USER(1202, "User not found", HttpStatus.NOT_FOUND),
    USER_BLOCKED(1203, "User blocked", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1204, "Emal not found", HttpStatus.NOT_FOUND),
    EMAIL_EXISTS(1205, "Email already exists", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_FOUND(1206, "Account not found for user", HttpStatus.NOT_FOUND),
    PHONE_NOT_FOUND(1207, "Phone not found", HttpStatus.BAD_REQUEST),
    PHONE_EXISTS(1208, "Phone already exists", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(1209, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1210, "User not found", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND(1212, "Address not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1213, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXISTED(1214, "Category name already existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1215, "Product not found", HttpStatus.NOT_FOUND),
    INVALID_QUANTITY(1216, "Invalid quantity", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(1217, "Cart not found", HttpStatus.NOT_FOUND),
    CART_EMPTY(1218, "Cart empty", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1219, "Order not found", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK(1220, "Out of stock", HttpStatus.BAD_REQUEST),

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}


