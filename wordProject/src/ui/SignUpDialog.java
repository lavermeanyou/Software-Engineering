package ui;

import javax.swing.*;
import java.awt.*;

public class SignUpDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signUpButton;
    private boolean isSuccess = false;

    public SignUpDialog(JFrame owner) {
        super(owner, "회원가입", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("사용자 이름:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("비밀번호:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("비밀번호 확인:"), gbc);
        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        // Button
        signUpButton = new JButton("가입하기");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(signUpButton, gbc);
        
        signUpButton.addActionListener(e -> {
            if (!validateInput()) {
                isSuccess = false;
                return;
            }
            isSuccess = true;
            setVisible(false);
        });

        pack();
        setLocationRelativeTo(owner);
    }
    
    private boolean validateInput() {
        String username = getUsername();
        String password = getPassword();
        String confirm = getConfirmPassword();
        
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 채워주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // CSV 파일 저장 형식이므로 쉼표 포함 금지
        if (username.contains(",") || password.contains(",")) {
            JOptionPane.showMessageDialog(this, "사용자 이름이나 비밀번호에 쉼표(,)를 포함할 수 없습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public String getUsername() { return usernameField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public String getConfirmPassword() { return new String(confirmPasswordField.getPassword()); }
    public boolean isSuccess() { return isSuccess; }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        isSuccess = false;
    }
}