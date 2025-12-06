// model/User.java (수정된 최종 버전)
package model;

import java.util.function.Consumer;

public class User {
    private String username;
    private int level;
    private int exp;

    private Consumer<User> saveCallback; 

    public User(String username) {
        this.username = username;
        this.level = 1;
        this.exp = 0;
    }
    
    // 기존 정보를 로드하기 위한 생성자
    public User(String username, int level, int exp) {
        this.username = username;
        this.level = level;
        this.exp = exp;
    }
    
    // New: 콜백을 설정하는 메서드 (데이터 저장 캡슐화 용도)
    public void setSaveCallback(Consumer<User> callback) {
        this.saveCallback = callback;
    }

    // 경험치 추가
    public void addExp(int exp) {
        this.exp += exp;
        checkLevelUp();
        
        // 경험치나 레벨이 변경된 후 콜백을 호출하여 저장 로직을 트리거
        if (this.saveCallback != null) {
            this.saveCallback.accept(this);
        }
    }

    // 레벨업 확인
    private void checkLevelUp() {
        int requiredExp = level * 10;
        while (this.exp >= requiredExp) {
            this.exp -= requiredExp;
            level++;
            requiredExp = level * 10;
            // Main.java에서 이 정보를 참조하는 코드가 있습니다.
            System.out.println(username + "님 레벨업! 현재 레벨: " + level);
        }
    }

    // Getter 메서드 (오류 해결을 위해 필수)
    public String getUsername() { return username; } // Missing in compilation
    public int getLevel() { return level; }         // Missing in compilation
    public int getExp() { return exp; }
}