package trabalho1.pp;

import java.util.Random;

public class Trabalho1Pp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Random r = new Random();
        int n1,n2,n3; // tamanho dos vetores gerados
        
        n1 = r.nextInt(1000000);
        n2 = r.nextInt(1000000);
        n3 = r.nextInt(1000000);
        
        // generate a uniformly distributed int random numbers
        int[] integers = new int[n1];
        for (int i = 0; i < integers.length; i++) {
          integers[i] = r.nextInt();
        }
        
        // generate a uniformly distributed float random numbers
        float[] floats = new float[n2];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = r.nextFloat();
        }
        
        // generate a Gaussian normally distributed random numbers
        double[] gaussians = new double[n3];
        double MEAN = 100.0f; 
        double VARIANCE = 50.0f;
        for (int i = 0; i < gaussians.length; i++) {
            gaussians[i] = MEAN + r.nextGaussian()*VARIANCE ;
        }
        
        System.out.println(n1+"/"+n2+"/"+n3);

        
    }
    
}
