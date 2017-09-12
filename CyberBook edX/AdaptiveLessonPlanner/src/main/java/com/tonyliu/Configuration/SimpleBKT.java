package com.tonyliu.Configuration;


/**
 * Created by tao on 8/31/17.
 * This class will compute the L value, assume the four initial parameters are given: P(L0), P(G), P(S), P(T)
 *
 *
 */

public class SimpleBKT {

    private Double Lzero;
    private Double G;
    private Double S;
    private Double T;
    private Integer correctness;

    public SimpleBKT(Double Lzero, Double G, Double S, Double T, Integer correctness) {
        this.Lzero = Lzero;
        this.G = G;
        this.S = S;
        this.T = T;
        this.correctness = correctness;
    }

    // compute the P(L|Correct) first then update the P(L)
    public Double computeL() {
        double prevLgivenresult = 0;
        if(correctness == 1) {
            prevLgivenresult = ((Lzero * (1.0 - S)) / ((Lzero * (1.0 - S)) + ((1.0 - Lzero) * (G))));
        } else {
            prevLgivenresult = ((Lzero * (S)) / ((Lzero * (S)) + ((1.0 - Lzero) * (1.0 - G))));
        }

        double newL = prevLgivenresult + (1.0 - prevLgivenresult) * T;
        return newL;
    }

}
