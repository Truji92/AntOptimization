package util;

import java.io.*;
import java.util.*;

public class AntUtils {

    public int NUM_CIUDADES;

    private int mejor_local = -1;
    private int mejor_global = -1;
    private int ev = 0;

    private Vector<Double> coord1;
    private Vector<Double> coord2;

    Vector<Integer> costes;

    private int matrizCosto[][];
    private double matrizHeur[][];
    private double matrizFerom[][];
    private int visitados[][];

    public AntUtils(String fichero) {
        coord1 = new Vector<Double>();
        coord2 = new Vector<Double>();
        costes = new Vector<Integer>();
        LeerFichero(fichero);
        NUM_CIUDADES = coord1.size();
        matrizCosto = new int[NUM_CIUDADES][NUM_CIUDADES];
        matrizHeur = new double[NUM_CIUDADES][NUM_CIUDADES];
        matrizFerom = new double[NUM_CIUDADES][NUM_CIUDADES];
        //visitados = new int[HORMIGAS][NUM_CIUDADES];
    }

    public void LeerFichero(String fichero) {
        File fich;
        FileReader fr = null;
        BufferedReader br;
        int comienzo, medio = 0;
        String coord1s, coord2s;
        double aux1, aux2;
        try {
            // Abrir el fichero. Creamos el BuferedReader para poder
            // leer l�neas completas.
            fich = new File(fichero);
            fr = new FileReader(fich);
            br = new BufferedReader(fr);

            // Leemos el fichero.
            String linea;
            // Leemos las 6 primeras l�neas pero no hacemos nada con ellas
            // Las queremos filtrar.
            for (int i = 0; i < 6; i++) {
                linea = br.readLine();
            }
            while ((linea = br.readLine()) != null) {
                // Antes de mostrar la l�nea, le quitamos el numerito de delante
                // Guardamos la posici�n del primer blanco
                // Le sumamos 1 porque nos interesa la posici�n
                // siguiente del blanco.
                comienzo = linea.indexOf(" ") + 1;
                // Guardamos la l�nea ya sin el numerito de delante
                linea = linea.substring(comienzo);
                // Buscamos el siguiente blanco
                medio = linea.indexOf(" ") + 1;
                // Formamos un nuevo string desde la posici�n 0
                // hasta el siguiente blanco
                coord1s = linea.substring(0, medio);
                // Y otro desde la siguiente posici�n del siguiente blanco
                // hasta el final del string
                coord2s = linea.substring(medio);
                // Guardamos en los atributos de la clase
                // los valores en su vector correspondiente
                if (!coord1s.isEmpty() && !coord2s.isEmpty()) {
                    aux1 = Double.parseDouble(coord1s);
                    aux2 = Double.parseDouble(coord2s);
                    coord1.addElement(aux1);
                    coord2.addElement(aux2);
                }
            }
            //Prueba para ver si salen los valores
            //Nota: no salen todos porque no cabe en la consola
			/*for (int k = 0;k < coord1.size();k++)
				System.out.println(coord1.elementAt(k));
			for (int k = 0;k < coord2.size();k++)
				System.out.println(coord2.elementAt(k));*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta
            // una excepci�n.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }


    }

//    public void lanzar() {
//        calculaMatrices();
//        double ferom_ini = 1 / (NUM_CIUDADES * greedy(CIUDAD_INICIAL));
//        iniMatrizFerom(ferom_ini);
//        //mostrarMatrices();
//        while (true) {
//            paso1();
//            paso2();
//            paso3();
//            paso4();
//            paso5();
//            for (int i = 0; i < HORMIGAS; i++)
//                for (int j = 0; j < NUM_CIUDADES; j++)
//                    visitados[i][j] = -1;
//            costes.clear();
//        }
//    }

    public void calculaMatrices() {
        double x, y;
        // Calcular x = coord1(i) - coord1(j)
        for (int i = 0; i < coord1.size(); i++) {
            for (int j = 0; j < coord1.size(); j++) {
                if (i != j) {
                    x = coord1.elementAt(i) - coord1.elementAt(j);
                    y = coord2.elementAt(i) - coord2.elementAt(j);
                    matrizCosto[i][j] = (int) Math.rint(Math.sqrt(x * x + y * y));
                    matrizHeur[i][j] = (double) 1 / matrizCosto[i][j];
                } else {
                    matrizCosto[i][j] = 0;
                    matrizHeur[i][j] = 0;
                }
            }
        }
    }

    public Vector<Double> getCoord1() {
        return coord1;
    }

    public Vector<Double> getCoord2() {
        return coord2;
    }

    public int[][] getMatrizCosto() {
        return matrizCosto;
    }

    public void mostrarMatrices() {
		/*System.out.println("MATRIZ COSTO");
		for (int i=0;i<matrizCosto.length;i++){
			for(int j=0;j<matrizCosto.length;j++){
				System.out.print(matrizCosto[i][j] + "\t");
			}
			System.out.print("\n");
		}
		System.out.println("\nMATRIZ HEURISTICA");
		for (int i=0;i<matrizHeur.length;i++){
			for(int j=0;j<matrizHeur.length;j++){
				System.out.print(matrizHeur[i][j] + "\t");
			}
			System.out.print("\n");
		}*/
        System.out.println("\nMATRIZ FEROMONAS");
        for (int i = 0; i < matrizFerom.length; i++) {
            for (int j = 0; j < matrizFerom.length; j++) {
                System.out.print(matrizFerom[i][j] + "\t");
            }
            System.out.print("\n");
        }
    }

    public class Sol {
        public int coste;
        public Vector<Integer> solution;
        public Sol(int coste, Vector<Integer> solution) {
            this.coste = coste;
            this.solution = solution;
        }
    }

    public Sol greedy(int ciudad_inicial) {
        double coste_total = 0;
        double min = -1;
        int cs = ciudad_inicial;
        int ciudad_siguiente = ciudad_inicial;
        Vector<Integer> visitados = new Vector<Integer>();
        visitados.add(cs);

        while (visitados.size() < matrizCosto.length) {
            for (int j = 0; j < matrizCosto.length; j++) {
                if ((min == -1 || matrizCosto[cs][j] < min) && (cs != j) && (!visitados.contains(j))) {
                    min = matrizCosto[cs][j];
                    ciudad_siguiente = j;
                }
            }
            // Decimos cual es la ciudad siguiente
            cs = ciudad_siguiente;
            // Marcamos esa ciudad como visitada
            visitados.add(cs);
            // Actualizamos el coste
            coste_total += min;
            // Reseteamos el min para la siguiente fila
            min = -1;
        }
        // Le sumamos el coste desde la ultima ciudad a la inicial de nuevo
        // para cerrar el trayecto
        coste_total = coste_total + matrizCosto[cs][ciudad_inicial];

        return new Sol((int)coste_total, visitados);
    }

    public void iniMatrizFerom(double ferom_ini) {
        for (int i = 0; i < NUM_CIUDADES; i++)
            for (int j = 0; j < NUM_CIUDADES; j++)
                matrizFerom[i][j] = ferom_ini;
    }

//    public int transicion(int visitados[], int r) {
//        //Todo
//        return ciudad;
//    }

}