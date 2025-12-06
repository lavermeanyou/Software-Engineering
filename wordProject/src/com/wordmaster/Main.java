package com.wordmaster;

import model.*;
import service.*;
import ui.*;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // CSV 읽기 (resources를 Source Folder로 등록했고 파일명만 넘깁니다)
        CSVLoader loader = new CSVLoader();
        List<Word> words = loader.loadWords("words.csv");

        // 사용자/세션/통계
        final User[] userRef = new User[1]; // 현재 로그인한 사용자
        final Session[] sessionRef = new Session[1];
        final Stats[] statsRef = new Stats[1];
        statsRef[0] = new Stats();

        // 서비스
        AnswerChecker answerChecker = new AnswerChecker();
        AccuracyTracker accuracyTracker = new AccuracyTracker();
        WrongWordTracker wrongWordTracker = new WrongWordTracker();
        
        AuthService authService = new AuthService(); 

        // UI
        MainFrame frame = new MainFrame();
        LoginPanel login = frame.getLoginPanel(); 
        StartPanel start = frame.getStartPanel();
        QuestionPanel qPanel = frame.getQuestionPanel();
        AnswerPanel aPanel = frame.getAnswerPanel();
        FeedbackPanel fPanel = frame.getFeedbackPanel();
        StatsPanel sPanel = frame.getStatsPanel();
        ProfilePanel pPanel = frame.getProfilePanel();
        SignUpDialog signUpDialog = new SignUpDialog(frame); 

        final Word[] current = new Word[1];

        // ---------------------------------------------------------------------
        // ▶ 로그인/회원가입 이벤트 처리
        // ---------------------------------------------------------------------

        // 회원가입 버튼
        login.getSignupButton().addActionListener(e -> {
            signUpDialog.clearFields();
            signUpDialog.setVisible(true);
            
            if (signUpDialog.isSuccess()) {
                String username = signUpDialog.getUsername();
                String password = signUpDialog.getPassword();
                
                if (authService.signUp(username, password)) {
                    JOptionPane.showMessageDialog(frame, "회원가입 성공! 로그인해주세요.", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "회원가입 실패! 사용자 이름이 중복되거나 파일 오류입니다.", "실패", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // 로그인 버튼
        login.getLoginButton().addActionListener(e -> {
            String username = login.getUsername();
            String password = login.getPassword();
            
            User loggedInUser = authService.login(username, password); 
            
            if (loggedInUser != null) {
                userRef[0] = loggedInUser;
                
                userRef[0].setSaveCallback(user -> authService.saveUser(user)); 
                
                pPanel.updateProfile(userRef[0]); // 프로필 업데이트
                frame.showStart(); // 시작 화면으로 전환
                login.clearFields();
                JOptionPane.showMessageDialog(frame, loggedInUser.getUsername() + "님, 환영합니다! (레벨: " + loggedInUser.getLevel() + ")", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "로그인 실패: 사용자 이름 또는 비밀번호를 확인해주세요.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---------------------------------------------------------------------
        // "새 게임 시작" 버튼
        // ---------------------------------------------------------------------
        start.getNewGameButton().addActionListener(e -> {
            if (userRef[0] == null) {
                JOptionPane.showMessageDialog(frame, "로그인이 필요합니다.", "경고", JOptionPane.WARNING_MESSAGE);
                frame.showLogin();
                return;
            }
            
            sessionRef[0] = new Session(userRef[0], words); // 새 세션 (로그인된 사용자 사용)
            statsRef[0]   = new Stats();              // 통계 리셋
            current[0]    = sessionRef[0].getNextWord();

            if (current[0] != null) qPanel.setWord(current[0]);
            aPanel.clearInput();
            sPanel.updateStats(statsRef[0]);
            pPanel.updateProfile(userRef[0]); // 프로필 업데이트

            frame.showQuiz(); // 문제+입력창 카드로 전환
            SwingUtilities.invokeLater(() -> aPanel.getAnswerField().requestFocusInWindow());
        });

        aPanel.getSubmitButton().addActionListener(e -> {
            if (current[0] == null || userRef[0] == null) return;

            char input = aPanel.getInputChar();
            boolean ok = answerChecker.checkAnswer(current[0], input);
            
            if (ok) {
                userRef[0].addExp(1); 
            }
            
            fPanel.showFeedback(ok,
                String.valueOf(current[0].getEnglish().charAt(current[0].getBlankIndex())));
            accuracyTracker.updateStats(statsRef[0], ok);
            if (!ok) wrongWordTracker.recordWrongWord(current[0]);

            sPanel.updateStats(statsRef[0]);
            pPanel.updateProfile(userRef[0]); // 프로필 정보 갱신

            Word next = sessionRef[0].getNextWord();
            if (next != null) {
                current[0] = next;
                qPanel.setWord(current[0]);
                aPanel.clearInput();
                aPanel.getAnswerField().requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(frame, "모든 문제가 완료되었습니다!");
                frame.showStart(); // 시작 화면으로 복귀
            }
        });

        // 처음에는 로그인 화면
        frame.showLogin();
    }
}