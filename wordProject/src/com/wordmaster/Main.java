package com.wordmaster;

import model.*;
import service.*;
import ui.*;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

// (MainFrame, LoginPanel, SignUpDialog, AuthService 등의 클래스가 존재함을 가정합니다)

public class Main {
    public static void main(String[] args) {
        // CSV 읽기 (Slang 모드 지원을 위해 words.csv와 slang.csv 로드)
        CSVLoader loader = new CSVLoader();
        List<Word> normalWords = loader.loadWords("words.csv");
        List<Word> slangWords = loader.loadWords("slang.csv");

        // 사용자/세션/통계
        final User[] userRef = new User[1]; 
        final Session[] sessionRef = new Session[1];
        final Stats[] statsRef = new Stats[1];
        statsRef[0] = new Stats();

        // 서비스
        AnswerChecker answerChecker = new AnswerChecker();
        AccuracyTracker accuracyTracker = new AccuracyTracker();
        WrongWordTracker wrongWordTracker = new WrongWordTracker();
        
        // 인증 서비스 (실제 구현 필요)
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

        // 현재 문제를 익명리스너에서 바꾸기 위해 배열로 보관
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
                // userRef[0].setSaveCallback(user -> authService.saveUser(user)); 
                
                pPanel.updateProfile(userRef[0]); 
                frame.showStart(); 
                login.clearFields();
                JOptionPane.showMessageDialog(frame, loggedInUser.getUsername() + "님, 환영합니다! (레벨: " + loggedInUser.getLevel() + ")", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "로그인 실패: 사용자 이름 또는 비밀번호를 확인해주세요.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---------------------------------------------------------------------
        // ▶ 게임 시작 및 진행 이벤트 처리
        // ---------------------------------------------------------------------

        // "새 게임 시작" 버튼
        start.getNewGameButton().addActionListener(e -> {
            if (userRef[0] == null) {
                JOptionPane.showMessageDialog(frame, "게임을 시작하려면 먼저 로그인해야 합니다.", "로그인 필요", JOptionPane.WARNING_MESSAGE);
                frame.showLogin();
                return;
            }
            
            List<Word> selectedWords;
            if (start.isSlangModeSelected()) { // Slang Mode 체크
                selectedWords = slangWords;
                if (selectedWords.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "슬랭 데이터가 없습니다!", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                selectedWords = normalWords;
                if (selectedWords.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "일반 단어 데이터가 없습니다!", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            sessionRef[0] = new Session(userRef[0], selectedWords);
            statsRef[0]   = new Stats();              
            
            current[0]    = sessionRef[0].getNextWord();

            if (current[0] != null) qPanel.setWord(current[0]);
            fPanel.clearFeedback(); 
            aPanel.clearInput();
            sPanel.updateStats(statsRef[0]);
            pPanel.updateProfile(userRef[0]);

            frame.showQuiz(); 
        });

        // "제출" 버튼
        aPanel.getSubmitButton().addActionListener(e -> {
            if (current[0] == null) return;
            if (userRef[0] == null) return;

            String input = aPanel.getInputText(); 
            boolean ok = answerChecker.checkAnswer(current[0], input); 
            
            String correctAnswer;
            
            if (current[0].isSlang()) {
                // 슬랭 모드: 정답은 한글 뜻
                correctAnswer = current[0].getMeaning();
            } else {
                // [일반 단어장 모드] 정답은 빈칸에 들어갈 영문자
                correctAnswer = String.valueOf(current[0].getCorrectChar());
            }

            // 피드백 로직 분기
            if (ok) {
                fPanel.showFeedback(true, correctAnswer); 
            } else {
                if (current[0].isSlang()) {
                    // 슬랭 오답: 팝업 메시지와 해설 (슬랭 구조 유지)
                    String fullExplanation = current[0].getFullExplanation(); 
                    String message = String.format("오답! 정답: %s\n\n해설: %s", 
                                                 correctAnswer, 
                                                 fullExplanation.isEmpty() ? "해설 없음" : fullExplanation); 

                    JOptionPane.showMessageDialog(frame, message, "오답 해설", JOptionPane.ERROR_MESSAGE);
                    fPanel.showFeedback(false, correctAnswer); 
                } else {
                    // 일반 단어장 모드 오답: FeedbackPanel에 영문자 정답 표시 (기존 동작 복구)
                    fPanel.showFeedback(false, correctAnswer); 
                }
            }
            
            accuracyTracker.updateStats(statsRef[0], ok);
            if (!ok) wrongWordTracker.recordWrongWord(current[0]);
            
            // 경험치 추가
            userRef[0].addExp(ok ? 10 : 5); 

            sPanel.updateStats(statsRef[0]);
            pPanel.updateProfile(userRef[0]); 

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

        // 처음에는 로그인 화면을 표시합니다.
        frame.showLogin(); 
    }
}