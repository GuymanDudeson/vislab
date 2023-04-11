package de.hska.iwi.vislab.lab1.example.ws;
import javax.jws.*;

@WebService
public interface FibonacciServiceIntf {
    public int getFibonacci(int i); // interface method (does not have a body)
  }