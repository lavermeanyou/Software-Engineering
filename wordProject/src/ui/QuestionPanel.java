package ui;

import model.Word;

import javax.swing.*;
import java.awt.*;

public class QuestionPanel extends JPanel {
    private JLabel wordLabel;

    public QuestionPanel() {
        setLayout(new BorderLayout());
        wordLabel = new JLabel("문제가 여기에 표시됩니다", SwingConstants.CENTER);
        wordLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 36)); // 한글 글꼴
        add(wordLabel, BorderLayout.CENTER);
    }

    public void setWord(Word word) {
        if (word != null) {
            if (word.isSlang()) {
                // 슬랭 모드: 0번째 인덱스(English)만 표시
                wordLabel.setText(word.getDisplayWord()); 
            } else {
                // 일반 모드: 빈칸 처리된 단어 + 뜻을 함께 표시
                wordLabel.setText(word.getDisplayWord() + "  (" + word.getMeaning() + ")");
            }
        } else {
            wordLabel.setText("문제가 없습니다");
        }
    }

    public JLabel getWordLabel() { return wordLabel; }
}