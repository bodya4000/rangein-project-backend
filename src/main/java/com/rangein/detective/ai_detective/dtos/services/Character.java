package com.rangein.detective.ai_detective.dtos.services;

public record Character(

        String n,   // name
        String g,   // gender ("m" / "f")
        int    a,   // age
        String r,    // role
        String op
) { }