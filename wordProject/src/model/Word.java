package model;

public class Word {
    private String english;
    private String meaning;
    private int blankIndex; // 빈칸 위치 (0부터 시작)
    private int correctCount;
    private int wrongCount;
    private boolean isSlang; 

    // 슬랭 모드의 2번째 인덱스(상세 해설)를 저장하는 필드
    private String fullExplanation; 

    public Word(String english, String meaning, int blankIndex) {
        this.english = english;
        this.meaning = meaning;
        this.blankIndex = blankIndex;
        this.correctCount = 0;
        this.wrongCount = 0;
        this.isSlang = false;
        this.fullExplanation = "";
    }

    public void setSlang(boolean isSlang) {
        this.isSlang = isSlang;
    }
    
    public void setFullExplanation(String fullExplanation) {
        this.fullExplanation = fullExplanation;
    }

    // [일반 모드 정답 로직] 사용자가 입력한 문자와 빈칸 문자가 맞는지 확인
    public boolean checkAnswer(char c) {
        return english.charAt(blankIndex) == c;
    }
    
    // [슬랭 모드 정답 로직] 사용자가 입력한 문자열과 정답 (meaning)을 비교하는 메서드
    public boolean checkAnswer(String input) {
        return meaning.trim().equalsIgnoreCase(input.trim());
    }

    // 정답 카운트 증가
    public void incrementCorrect() {
        correctCount++;
    }

    // 오답 카운트 증가
    public void incrementWrong() {
        wrongCount++;
    }

    // 문제 표시용 문자열 (슬랭 모드에 따라 다르게 표시)
    public String getDisplayWord() {
        if (isSlang) {
            // 슬랭 모드: 0번째 인덱스(English)만 표시
            return english;
        } else {
            // 일반 모드: 빈칸 처리
            StringBuilder sb = new StringBuilder(english);
            sb.setCharAt(blankIndex, '(');
            sb.insert(blankIndex + 1, ')');
            return sb.toString();
        }
    }
    
    // [일반 모드 정답] 빈칸에 들어갈 정답 문자(char)를 반환하는 메서드
    public char getCorrectChar() {
        if (blankIndex >= 0 && blankIndex < english.length()) {
            return english.charAt(blankIndex);
        }
        return '\0'; 
    }

    // Getter 메서드
    public String getEnglish() { return english; }
    public String getMeaning() { return meaning; }
    public int getBlankIndex() { return blankIndex; }
    public int getCorrectCount() { return correctCount; }
    public int getWrongCount() { return wrongCount; }
    public boolean isSlang() { return isSlang; }
    public String getFullExplanation() { return fullExplanation; }
}