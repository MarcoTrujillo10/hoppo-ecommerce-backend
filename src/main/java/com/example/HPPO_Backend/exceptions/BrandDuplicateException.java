package com.example.HPPO_Backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "La marca que se intenta agregar esta duplicada")
public class BrandDuplicateException extends Exception {
}
