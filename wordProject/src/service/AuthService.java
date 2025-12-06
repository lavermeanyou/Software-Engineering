package service;

import model.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

public class AuthService {

    private static final String USER_FILE_NAME = "users.csv";
    private String userFilePath; 

    public AuthService() {
        try {
            URL resourceRootUrl = getClass().getClassLoader().getResource("");
            
            if (resourceRootUrl != null) {
                userFilePath = new File(resourceRootUrl.toURI().getPath(), USER_FILE_NAME).getAbsolutePath();
            } else {
                userFilePath = USER_FILE_NAME; 
            }
        } catch (Exception e) {
            System.err.println("사용자 파일 경로 설정 중 오류 발생. 현재 디렉토리에 저장됩니다: " + e.getMessage());
            userFilePath = USER_FILE_NAME;
        }
        
        System.out.println("사용자 정보는 다음 경로에 저장됩니다: " + userFilePath);

        // 파일이 없으면 헤더와 함께 생성 (level, exp 필드 추가)
        File file = new File(userFilePath);
        if (!file.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                bw.write("username,password,level,exp\n"); // Level, Exp 필드 추가
            } catch (IOException e) {
                System.err.println("사용자 파일 생성 실패: " + e.getMessage());
            }
        }
    }

    // 사용자 정보 로드 (List<String[]> 형태로 로드: [username, password, level, exp] 쌍)
    private List<String[]> loadAllUserRecords() {
        List<String[]> users = new ArrayList<>(); 
        try (BufferedReader br = new BufferedReader(new FileReader(userFilePath, StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // 헤더 스킵
                String[] parts = line.split(",");
                // [0]: username, [1]: password, [2]: level, [3]: exp
                if (parts.length >= 4) { 
                    users.add(new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()});
                }
            }
        } catch (IOException e) {
            System.err.println("사용자 파일 로드 실패: " + e.getMessage());
        }
        return users;
    }

    private void saveAllUserRecords(List<String[]> records) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(userFilePath, StandardCharsets.UTF_8, false))) {
            bw.write("username,password,level,exp\n"); // 헤더 재작성
            for (String[] record : records) {
                // [0]: username, [1]: password, [2]: level, [3]: exp
                bw.write(String.format("%s,%s,%s,%s", record[0], record[1], record[2], record[3]));
                bw.newLine();
            }
        }
    }

    /**
     * 회원 가입
     * @return 성공 여부 (true: 성공, false: 중복 또는 실패)
     */
    public boolean signUp(String username, String password) {
        List<String[]> users = loadAllUserRecords();
        
        for (String[] user : users) {
            if (user[0].equals(username)) {
                return false; // ID 중복
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(userFilePath, StandardCharsets.UTF_8, true))) { 
            // 비밀번호를 평문으로 저장, level=1, exp=0으로 초기화하여 저장
            bw.write(String.format("%s,%s,1,0", username, password)); 
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 로그인 (레벨/경험치 로드 포함)
     * @return 로그인 성공 시 User 객체, 실패 시 null
     */
    public User login(String username, String password) {
        List<String[]> users = loadAllUserRecords();
        
        for (String[] userRecord : users) {
            String storedUsername = userRecord[0];
            String storedPassword = userRecord[1];

            if (storedUsername.equals(username) && storedPassword.equals(password)) {
                // level, exp 파싱 및 User 객체 생성
                try {
                    int level = Integer.parseInt(userRecord[2]);
                    int exp = Integer.parseInt(userRecord[3]);
                    return new User(username, level, exp); 
                } catch (NumberFormatException e) {
                    System.err.println("사용자 데이터 Level/Exp 파싱 오류: " + storedUsername);
                    return new User(username); // 오류 시 기본값으로 생성
                }
            }
        }
        return null; // 로그인 실패
    }
    
    /**
     * 사용자 진행 상태 (Level/Exp)를 파일에 저장합니다.
     * @param user 저장할 User 객체
     * @return 성공 여부
     */
    public boolean saveUser(User user) {
        List<String[]> records = loadAllUserRecords();
        boolean updated = false;

        for (int i = 0; i < records.size(); i++) {
            String[] record = records.get(i);
            if (record[0].equals(user.getUsername())) {
                // 해당 레코드의 level, exp 필드를 업데이트
                record[2] = String.valueOf(user.getLevel());
                record[3] = String.valueOf(user.getExp());
                records.set(i, record); 
                updated = true;
                break;
            }
        }

        if (updated) {
            try {
                // 전체 레코드를 파일에 덮어씁니다.
                saveAllUserRecords(records);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("사용자 정보 파일 저장 실패!");
                return false;
            }
        }
        return false;
    }
}