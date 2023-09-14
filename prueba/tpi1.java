package prueba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class tpi1 {

	public static void main(String[] args) throws IOException{
		
		String file = args[0];
		File a = new File(file);
		
		float entropia;
		byte[] bytes = archivoAVectorBytes(a);
		float[][] matrizDatos = new float[2][2]; // Primera columna tiene cantidad de 0 y 1, la otra columna tiene las probabilidades
		float[][] matrizCondicional = {{(float) 0.0,(float) 0.0},{(float) 0.0,(float) 0.0}}; // Prob 0/0, prob 0/1, prob 1/0, prob 1/1
		
		calculaProb(bytes, matrizDatos);
		calculaProbCondicional(bytes, matrizCondicional, matrizDatos);
		
		if (memoriaNula(matrizCondicional)) {
			System.out.println("Fuente de memoria nula\n");
			entropia = entropiaNula(matrizDatos);
			System.out.println("Entropía: "+entropia+"\n");
			if (args.length > 1) {
				int N;
				try {
					N = Integer.parseInt(args[1]);
					calculaExtensionOrdenN(matrizDatos, Integer.parseInt(args[2]));
				} catch (NumberFormatException e) {
					System.out.println("El valor de N debe ser un numero natural");
				}
			}
			
		} else {
			System.out.println("Fuente de memoria no nula. (Markov)\n");
			float[] vec = vecEst(matrizCondicional);
			entropia = entropiaNoNula(matrizCondicional, vec);
			System.out.println("Entropía: "+entropia+"\n");
			System.out.println("Vector estacionario: "+vec[0]+", "+vec[1]+"\n");
		}
	}
	
	public static byte[] archivoAVectorBytes(File arch) throws IOException {
		FileInputStream arch1 = null;
		try {
			arch1 = new FileInputStream(arch);
		} catch (FileNotFoundException e) {
			System.out.println("El archivo no existe o no es válido");
		}
		
		byte[] bytes = new byte[(int)arch.length()];
		
		arch1.read(bytes);
		arch1.close();
		
		return bytes;
	}
	
	public static void calculaProb(byte[] vecOriginal, float[][] datos) {
		byte aux = 0;
		byte[] vec = vecOriginal.clone();
		int cantUnos = 0, cantCeros = 0;
		for(int i=0; i < vec.length; i++) {
			for (int j=0; j<8; j++) {
				aux = (byte) (vec[i] & 0x01);
				vec[i] = (byte) (vec[i] >> 1);
				if (aux == 0)
					cantCeros++;
				else
					cantUnos++;
			}
		}
		datos[0][0] = cantCeros;
		datos[1][0] = cantUnos;
		datos[0][1] = cantCeros/(float)(vec.length*8);
		datos[1][1] = cantUnos/(float)(vec.length*8);
	}
	
	public static void calculaProbCondicional(byte[] vecOriginal, float[][] matriz, float[][] matrizDatos) {
		byte act , ant;
		byte[] vec = vecOriginal.clone();
		
		ant = (byte) (vec[0] & 0x80);
		ant = (byte) ((ant >> 7) & 0x01);
		vec[0] = (byte) (vec[0] << 1);
		for(int i=0; i < vec.length; i++) {
			
			for (int j=0; j<8; j++) {
				if (i == 0 && j==0)
					j++;
				act = (byte) (vec[i] & 0x80);
				act = (byte) ((act >> 7) & 0x01);
				
				vec[i] = (byte) (vec[i] << 1);
				
				matriz[act][ant]++;
				
				ant = act;
			}
		}
		matriz[0][0]= matriz[0][0]/ matrizDatos[0][0];
		matriz[0][1]= matriz[0][1]/ matrizDatos[1][0];
		matriz[1][0]= matriz[1][0]/ matrizDatos[0][0];
		matriz[1][1]= matriz[1][1]/ matrizDatos[1][0];
		
	
	}

	public static boolean memoriaNula(float[][] matrizCondicional) {
		
		float tol = (float) 0.05;
		
		if (Math.abs(matrizCondicional[0][0]-matrizCondicional[0][1]) > tol && Math.abs(matrizCondicional[1][0]-matrizCondicional[1][1]) > tol) {
			return true;
		} else
			return false;
	}
	
	public static float entropiaNula(float[][] matrizDatos) {
		
		float probCero = matrizDatos[0][1], probUno = matrizDatos[1][1];
		float entropia = (float) (((probCero)*(Math.log10(1/probCero))/Math.log10(2)) + ((probUno)*(Math.log10(1/probUno))/Math.log10(2)));
		
		return entropia;
	}
	
	
	public static float entropiaNoNula(float[][] matrizTransicion, float[] vecEst) {
		
		float sumatoria1 = (float) (vecEst[0]*(matrizTransicion[0][0]*(Math.log10(1/matrizTransicion[0][0])/Math.log10(2)) + matrizTransicion[1][0]*(Math.log10(1/matrizTransicion[1][0])/Math.log10(2))));
		float sumatoria2 = (float) (vecEst[1]*(matrizTransicion[0][1]*(Math.log10(1/matrizTransicion[0][1])/Math.log10(2)) + matrizTransicion[1][1]*(Math.log10(1/matrizTransicion[1][1])/Math.log10(2))));
		
		return sumatoria1+sumatoria2;
	}
	
	public static float[] vecEst(float[][] matrizTransicion) {
		
		float v0, v1;
		
		
		v0 = (matrizTransicion[0][1])/(matrizTransicion[0][1]+matrizTransicion[1][0]);
		v1 = 1-v0;
		
		float[] vec = {v0, v1}; 
		return vec;
	}
	
	
	public static void calculaExtensionOrdenN(float[][] matrizDatos, int N) {
		
		float[] vecProb = {matrizDatos[0][1], matrizDatos[1][1]};
		float entropia = 0;
		
		// Genera la extensión de orden N
        int[] indices = new int[N];
        int totalSecuencias = (int) Math.pow(vecProb.length, N);

        for (int i = 0; i < totalSecuencias; i++) {
            double probabilidad = 1.0;

            for (int j = 0; j < N; j++) {
                probabilidad *= vecProb[indices[j]];
            }

            entropia += probabilidad * (Math.log(probabilidad) / Math.log(2));

            // Imprime la secuencia y su probabilidad
            StringBuilder linea = new StringBuilder();
            for (int k = 0; k < N; k++) {
                linea.append(indices[k]).append(" ");
            }
            linea.append("= ").append(probabilidad);
            System.out.println(linea.toString());

            // Actualiza los índices para la siguiente secuencia
            indices[N - 1]++;

            for (int k = N - 1; k >= 0; k--) {
                if (indices[k] == vecProb.length) {
                    if (k == 0) {
                        break; // Se alcanzó el final de las secuencias
                    }
                    indices[k] = 0;
                    indices[k - 1]++;
                }
            }
        }

        // Calcula la entropía total
        entropia *= -1;
        System.out.println("Entropía total: " + entropia);
		
	}
}



