package service;

import model.Word;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {

    private List<Word> loadFromStream(InputStream is, boolean isSlang) {
        List<Word> wordList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true; // 헤더 건너뛰기
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String english = parts[0].trim();
                    String meaning = parts[1].trim();
                    String thirdColumn = parts[2].trim(); 
                    
                    int blankIndex; 

                    if (isSlang) {
                        blankIndex = 0; 
                    } else {
                        try {
                            blankIndex = Integer.parseInt(thirdColumn);
                        } catch (NumberFormatException e) {
                            System.err.println("CSV 파싱 오류: difficulty 필드(3번째)가 숫자가 아닙니다. - " + thirdColumn);
                            blankIndex = 0; 
                        }
                    }
                    
                    Word newWord = new Word(english, meaning, blankIndex);
                    newWord.setSlang(isSlang); 
                    
                    if (isSlang) {
                        newWord.setFullExplanation(thirdColumn); 
                    }
                    
                    wordList.add(newWord);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }

    public List<Word> loadWords(InputStream is) {
        return loadFromStream(is, false);
    }

    public List<Word> loadWords(String filename) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            System.err.println("파일을 찾을 수 없습니다: " + filename);
            return new ArrayList<>();
        }
        
        boolean isSlang = filename.toLowerCase().contains("slang");
        
        return loadFromStream(is, isSlang);
    }
}