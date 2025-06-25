import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args)  {


        List<Jezyk> trening = new ArrayList<>();
        List<Jezyk> test = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        String folderPath = "/Users/jakub/Desktop/Siec/Teksty";

        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            List<Path> filePaths = paths.filter(Files::isRegularFile).toList();

            for (Path filePath : filePaths) {
                float [] tab = new float[26];
                read(trening,tab , filePath.toString());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("0 - zbada zbior testowy , 1 - podaj wlasny tekst ");

        int liczba = scanner.nextInt();
        float[] tab2 = new float[26];

        if (liczba == 0) {
            read(test, tab2, "/Users/jakub/Desktop/Siec/test/t1.txt");
        } else if (liczba == 1) {
            System.out.print("Enter your text: ");
            scanner.nextLine();
            String teskt = scanner.nextLine();
            readScanner(test,tab2,teskt);
        }
        float[] wagi_ang = new float[26];
        float[] wagi_it = new float[26];
        float[] wagi_pol = new float[26];

        Perceptron perceptronAng = new Perceptron(1, wagi_ang, 0.1f);
        Perceptron perceptronIt = new Perceptron(1, wagi_it, 0.1f);
        Perceptron perceptronPol = new Perceptron(1, wagi_pol, 0.1f);

        for (int i = 0; i < 100; i++) {
            for (Jezyk jezyk : trening) {
                perceptronAng.learn(jezyk.literki, jezyk.nazwa.equals("ang") ? 1 : 0);
                perceptronIt.learn(jezyk.literki, jezyk.nazwa.equals("it") ? 1 : 0);
                perceptronPol.learn(jezyk.literki, jezyk.nazwa.equals("pl") ? 1 : 0);
            }
        }


        System.out.println("\nTesting perceptrons:");
//        System.out.println(test.size());
        for (Jezyk jezyk : test) {
            int resultAng = perceptronAng.compute(jezyk.literki);
            int resultIt = perceptronIt.compute(jezyk.literki);
            int resultPol = perceptronPol.compute(jezyk.literki);

//            System.out.println(Arrays.toString(jezyk.literki));
            System.out.println("Text: " + jezyk.nazwa +
                    " -> English: " + resultAng +
                    ", Italian: " + resultIt +
                    ", Polish: " + resultPol);
        }
    }

    static void read(List<Jezyk> trening,float [] tab , String path){
        String nazwa = "";
        try(BufferedReader br = new BufferedReader(new FileReader(path))){
            nazwa = br.readLine();
            String linie = "" ;
            while ((linie = br.readLine())!= null){
                linie = linie.toLowerCase(Locale.ROOT);
                for (int i = 0; i < linie.length(); i++) {
                    if (linie.charAt(i) < 97 ||linie.charAt(i) > 122  ){
                        continue;
                    }
                    int i1 = (linie.charAt(i)) - 97;
                    tab[i1] += 1;
                }
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        int count2 = 0;
        for (int i = 0; i < tab.length; i++) {
          count2+= (int) tab[i];
        }
        for (int i = 0; i < tab.length; i++) {
            tab[i] = (float) (Math.round((tab[i] / count2) * 1000.0) / 1000.0);
        }
//        System.out.println(Arrays.toString(tab));

        Jezyk jezyk = new Jezyk(nazwa,tab);
        trening.add(jezyk);

    }
    static void readScanner(List<Jezyk> trening,float [] tab , String tekst){
        String linie = tekst.toLowerCase(Locale.ROOT);

        for (int i = 0; i < linie.length(); i++) {
            if (linie.charAt(i) < 97 ||linie.charAt(i) > 122  ){
                continue;
            }
            int i1 = (linie.charAt(i)) - 97;
            tab[i1] += 1;
        }
        int count2 = 0;
        for (int i = 0; i < tab.length; i++) {
            count2+= (int) tab[i];
        }
        for (int i = 0; i < tab.length; i++) {
            tab[i] = (float) (Math.round((tab[i] / count2) * 1000.0) / 1000.0);
        }
//        System.out.println(Arrays.toString(tab));

        Jezyk jezyk = new Jezyk("",tab);
        trening.add(jezyk);
    }

}

class Jezyk{

    String nazwa ;
    float [] literki;


    public Jezyk(String nazwa, float[] literki) {
        this.nazwa = nazwa;
        this.literki = literki;

    }
}

class Perceptron {

    float prog ;
    float [] wagi;
    float alfa ;

    public Perceptron(float prog, float[] wagi, float alfa) {
        this.prog = prog;
        this.wagi = wagi;
        this.alfa = alfa;
    }

    int compute(float[] wejscie){
        // wzór ∑ wjescia * wagi >= prog
        float suma = 0;
        for (int i = 0; i < wejscie.length; i++) {
            suma += wejscie[i]* this.wagi[i];
        }

        return suma >= prog ? 1 : 0 ;
    }

    void learn(float [] wejscie , int popr){
        // W' = W + (d-y) * X * α
        int podjeta = compute(wejscie);
        int blad = popr - podjeta;

        for (int i = 0; i < wagi.length; i++) {
            wagi[i] += wejscie[i]*blad*alfa;
        }

        prog -= blad*alfa;
    }


}