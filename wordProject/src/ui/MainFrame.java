package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private LoginPanel loginPanel; // New: 로그인 화면
    private StartPanel startPanel;
    private JPanel quizCard;          // <- 퀴즈 화면(복합 패널)

    private QuestionPanel questionPanel;
    private AnswerPanel answerPanel;
    private FeedbackPanel feedbackPanel;
    private StatsPanel statsPanel;
    private ProfilePanel profilePanel;

    public MainFrame() {
        setTitle("Word Master");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 패널 생성
        loginPanel = new LoginPanel();
        startPanel = new StartPanel();
        questionPanel = new QuestionPanel();
        answerPanel = new AnswerPanel();
        feedbackPanel = new FeedbackPanel();
        statsPanel = new StatsPanel();
        profilePanel = new ProfilePanel();

        // ▶ 사이드바 패널 생성 (Profile + Stats)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS)); // 세로 배치
        sidebarPanel.setPreferredSize(new Dimension(150, 0)); // 너비 지정
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.add(profilePanel);
        sidebarPanel.add(Box.createVerticalStrut(20)); // 간격
        sidebarPanel.add(statsPanel);
        sidebarPanel.add(Box.createVerticalGlue()); // 나머지 공간 채우기

        // ▶ QUIZ 카드(한 화면에 합치기)
        quizCard = new JPanel(new BorderLayout(0, 10));
        quizCard.add(feedbackPanel, BorderLayout.NORTH);   // 위: 정답/오답 피드백
        quizCard.add(questionPanel, BorderLayout.CENTER);  // 가운데: 문제
        quizCard.add(answerPanel, BorderLayout.SOUTH);     // 아래: 입력창+제출 버튼
        quizCard.add(sidebarPanel, BorderLayout.EAST);     // New: 사이드바 추가

        // 카드 등록
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(startPanel, "START");
        mainPanel.add(quizCard,   "QUIZ");

        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 화면 전환 유틸
    public void showLogin() { cardLayout.show(mainPanel, "LOGIN"); } // New
    public void showStart() { cardLayout.show(mainPanel, "START"); }
    public void showQuiz()  { 
        cardLayout.show(mainPanel, "QUIZ"); 
        getRootPane().setDefaultButton(answerPanel.getSubmitButton());
        // 입력창에 포커스 주기
        SwingUtilities.invokeLater(() -> answerPanel.getAnswerField().requestFocusInWindow());
    }

    // 필요시 getter
    public LoginPanel getLoginPanel() { return loginPanel; } // New
    public StartPanel getStartPanel() { return startPanel; }
    public QuestionPanel getQuestionPanel() { return questionPanel; }
    public AnswerPanel getAnswerPanel() { return answerPanel; }
    public FeedbackPanel getFeedbackPanel() { return feedbackPanel; }
    public StatsPanel getStatsPanel() { return statsPanel; }
    public ProfilePanel getProfilePanel() { return profilePanel; }
}