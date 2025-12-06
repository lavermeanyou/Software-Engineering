package service;

import model.Word;

public class AnswerChecker {

    // [수정] 입력 문자열과 단어 정답 확인 (일반/슬랭 분기)
    public boolean checkAnswer(Word word, String input) {
        boolean isCorrect;

        if (word.isSlang()) {
            // 슬랭 모드: 입력된 String 전체와 Word의 meaning(한글 뜻) 비교
            isCorrect = word.checkAnswer(input);
        } else {
            // [일반 단어장 모드] 입력된 String의 첫 글자(char)와 Word의 빈칸 문자 비교
            char inputChar = input.length() > 0 ? input.charAt(0) : '\0';
            isCorrect = word.checkAnswer(inputChar);
        }
        
        // 카운트 갱신
        if (isCorrect) {
            word.incrementCorrect();
        } else {
            word.incrementWrong();
        }
        return isCorrect;
    }
}