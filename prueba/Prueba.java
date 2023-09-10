package prueba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Prueba {

	public static void main(String[] args) throws IOException{
		File a = new File("C:\\Users\\Matias\\Desktop\\Facu\\Teoria_de_la_informacion\\tp1_sample0.bin");
		
		byte[] bytes = archivoAVectorBytes(a);
		float[][] matrizDatos = new float[2][2];
		float[][] matrizCondicional = {{(float) 0.0,(float) 0.0},{(float) 0.0,(float) 0.0}};
		
		calculaProb(bytes, matrizDatos);
		calculaProbCondicional(bytes, matrizCondicional, matrizDatos);
		System.out.println(matrizCondicional[0][0]+" "+matrizCondicional[0][1]);
		System.out.println(matrizCondicional[1][0]+" "+matrizCondicional[1][1]);
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
	
	public static void calculaProb(byte[] vecOriginal, float[][] prob) {
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
		prob[0][0] = cantCeros;
		prob[1][0] = cantUnos;
		prob[0][1] = cantCeros/(float)(vec.length*8);
		prob[1][1] = cantUnos/(float)(vec.length*8);
	}
	
	public static void calculaProbCondicional(byte[] vecOriginal, float[][] matriz, float[][] matrizDatos) {
		byte act , ant;
		byte[] vec = vecOriginal.clone();
		System.out.println(vec[0]);
		
		ant = (byte) (vec[0] & 0x80);
		ant = (byte) ((ant >> 7) & 0x01);
		vec[0] = (byte) (vec[0] << 1);
		for(int i=0; i < vec.length; i++) {
			
			for (int j=0; j<8; j++) {
				if (i == 0 && j==0)
					j++;
				act = (byte) (vec[i] & 0x80);
				act = (byte) ((act >> 7) & 0x01);
				if (i < 1) {
					System.out.println("anterior "+ant);
					System.out.println("actual "+act);
				}
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

}



