package com.ares.tienda.videojuegos.constantesSQL;

public class ConstantesSQL {

    public static final String SQL_GET_CARS_JSON =
            "select tc.id as \"id\", " +
                    "tc.title as \"title\", " +
                    "tc.price as \"price\", " +
                    "tc.brand as \"brand\", " +
                    "tc.description as \"description\", " +
                    "c.name as \"category\" " +
                    "from TABLA_CARS as tc, CATEGORY as c " +
                    "where tc.category_id = c.id";

    public static final String SQL_GET_CAR_JSON =
            SQL_GET_CARS_JSON + " and tc.id = :id";
}