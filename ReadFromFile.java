import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


public class ReadFromFile {

    public static class CompareWorkFrequency implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            // sort frequency in descending order
            int it = o2.getValue().compareTo(o1.getValue());
            if (it == 0) {
                // sort equally frequent items in alphabetical order
                it = o1.getKey().compareTo(o2.getKey()) ;
            }
            return it;
        }
    }


    private static List<String> getStopWords(String filename) throws IOException {
        List<String> stopWords = new LinkedList<>();

        FileReader fr = new FileReader(filename);
        BufferedReader bufr = new BufferedReader(fr);

        String line = bufr.readLine();

        while (line != null) {
            stopWords.add(line);
            line = bufr.readLine();
        }

        bufr.close();

        return stopWords;
    }

    private static Stream<String> getWordsAsList(String filename) throws IOException {
        return Files.lines(Paths.get(filename))
                .parallel()
                .flatMap(line -> Arrays.stream(line.trim().split(" ")))
                .map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase().trim())
                .filter(word -> word.length() > 0);
    }

    public static void main(String args[]) throws IOException {
        List<String> stopWords = getStopWords("stopwords.txt");

        Stream<String> words = getWordsAsList("usdeclar.txt");
//        Stream<String> words = getWordsAsList("alice30.txt");
//        Stream<String> words = getWordsAsList("kjbible.txt");
        Stemmer stemmer = new Stemmer();

        Map<String, Integer> wordCount = new HashMap<>();

        words
                .filter((word) -> !stopWords.contains(word))
                .map(stemmer::stemWord)
                .forEach((word) -> {
                    if (wordCount.containsKey(word)) {
                        int incCount = wordCount.get(word) + 1;
                        wordCount.put(word, incCount);
                    } else {
                        wordCount.put(word, 1);
                    }
                });


        List<Map.Entry<String, Integer>> list = new ArrayList<>(wordCount.entrySet());
        list.sort(new CompareWorkFrequency());
        list.subList(0, 10).forEach(System.out::println);
    }
}