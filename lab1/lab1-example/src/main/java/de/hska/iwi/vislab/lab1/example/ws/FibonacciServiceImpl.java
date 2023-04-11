package de.hska.iwi.vislab.lab1.example.ws;

import javax.jws.*;

@WebService(endpointInterface = "de.hska.iwi.vislab.lab1.example.ws.FibonacciServiceIntf")
public class FibonacciServiceImpl implements FibonacciServiceIntf {
    public int getFibonacci(int i){
        if (i <=1){
            return i;
        } else {
            int previousNumber = 0;
            int fibo = 1;
            for(int index = 2; index <= i; index++){
                int sum = fibo;
                fibo += previousNumber;
                previousNumber = sum;
            }
            return fibo;
        }
    }
}
