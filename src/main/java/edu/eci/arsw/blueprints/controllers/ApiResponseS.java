package edu.eci.arsw.blueprints.controllers;

public record ApiResponseS<T>(int code, String message, T data) {}
