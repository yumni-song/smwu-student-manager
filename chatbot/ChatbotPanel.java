package chatbot;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * ChatbotPanel 클래스는 챗봇 UI를 구성합니다.
 * 사용자가 채팅을 입력하고 전송 버튼을 누르면 채팅창에 표시되고,
 * AI 답변 채팅(예시)이 함께 생성되는 ui를 구현하였습니다.
 * 초기화 버튼을 누르면 채팅창이 초기화 됩니다.
 */

public class ChatbotPanel extends JPanel {
    private JPanel aiNamePanel; // 상단에 모델 이름 표시
    private JPanel viewPanel; // 채팅창
    private JScrollPane viewScrollPane; // 채팅창 스크롤
    private JPanel inputPanel; // 입력 패널
    private JTextArea inputArea; // 입력창
    private JButton sendButton, initializeButton; // 전송 버튼, 초기화 버튼
    private JLabel name, viewText; // 인공지능 모델 이름, 초기 메시지
    private Component top, bottom; // 초기 메시지 여백 설정

    private final Color bgColor = new Color(214, 240, 255);
    private final Color borderColor = new Color(144, 198, 224);
    private final Color textColor = new Color(48, 80, 96);


    public ChatbotPanel() {
        setLayout(new BorderLayout());
        top = Box.createVerticalGlue(); // 초기 메시지 여백
        bottom = Box.createVerticalGlue(); // 초기 메시지 여백

        // 인공지능 모델 이름 표시, 초기화 버튼
        aiNamePanel = new JPanel();
        aiNamePanel.setBackground(borderColor);
        aiNamePanel.setBorder(BorderFactory.createEmptyBorder(10,90,10,10));

        name = new JLabel("AI Chatbot"); // 인공지능 모델 이름 표시
        name.setFont(new Font("맑은 고딕", Font.BOLD, 18));

        initializeButton = new JButton(); // 초기화 버튼
        initializeButton.setPreferredSize(new Dimension(32,32));
        try {
            ImageIcon iniImage = new ImageIcon(getClass().getResource("/images/initialize.png"));
            Image iniImg = iniImage.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            initializeButton.setIcon(new ImageIcon(iniImg));
        } catch (Exception e) {
            // 이미지 로드 실패 시 텍스트로 대체
            initializeButton.setText("초기화");
            initializeButton.setForeground(textColor);
        }
        
        initializeButton.addActionListener(e -> { // 초기화 기능
            viewPanel.removeAll();
            viewPanel.revalidate();
            viewPanel.repaint();

            viewText = new JLabel("무엇이든 물어보세요"); // 초기 메시지
            viewText.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
            viewText.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewPanel.add(top);
            viewPanel.add(viewText);
            viewPanel.add(bottom);
        });

        aiNamePanel.setLayout(new BorderLayout());
        aiNamePanel.add(name, BorderLayout.CENTER);
        aiNamePanel.add(initializeButton, BorderLayout.EAST);
        
        // 채팅창
        viewPanel = new JPanel();
        viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));
        viewPanel.setBackground(bgColor);
        viewPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        viewScrollPane = new JScrollPane(viewPanel); // 스크롤 추가
        viewScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// 필요할 때만 수직 스크롤바 표시

        viewText = new JLabel("무엇이든 물어보세요"); // 초기 메시지
        viewText.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        viewText.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewPanel.add(top);
        viewPanel.add(viewText);
        viewPanel.add(bottom);
        
        // 채팅 입력 필드, 전송 버튼
        inputPanel = new JPanel();
        inputPanel.setBackground(borderColor);
        inputArea = new JTextArea(); // 텍스트 입력
        inputArea.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        inputArea.setLineWrap(true);
        inputArea.setColumns(30);

        sendButton = new JButton();
        sendButton.setPreferredSize(new Dimension(35,30));

        try {
            ImageIcon sendImage = new ImageIcon(getClass().getResource("/images/send.png"));
            Image sendImg = sendImage.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            sendButton.setIcon(new ImageIcon(sendImg));
        } catch (Exception e) {
            // 이미지 로드 실패 시 텍스트로 대체
            sendButton.setText("전송");
            sendButton.setForeground(textColor);
        }

        sendButton.addActionListener(new sendChat()); // 전송 기능

        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10,8,10,8));
        inputPanel.add(inputArea, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);


        add(aiNamePanel, BorderLayout.NORTH);
        add(viewScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    // 챗 전송하면 나타나는 채팅창 변화 및 AI 응답 예시 구현
    private class sendChat implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // 초기 메시지 제거
            if (viewText.getParent() == viewPanel) {
                viewPanel.remove(top);
                viewPanel.remove(viewText);
                viewPanel.remove(bottom);
                viewPanel.revalidate();
                viewPanel.repaint();
            }

            String inputText = inputArea.getText().trim();
            if(!inputText.isEmpty()){
                // 사용자 채팅
                JTextArea userMessage = new JTextArea(inputText);
                userMessage.setLineWrap(true);
                userMessage.setEditable(false);
                userMessage.setBackground(new Color(245, 240, 220));
                userMessage.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
                userMessage.setSize(130, Integer.MAX_VALUE);

                JPanel userMessageBox = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 사용자 입력 채팅 layout을 위한 패널
                userMessageBox.setOpaque(false);
                userMessageBox.add(userMessage);

                //viewPanel에 챗 메시지 추가
                viewPanel.add(userMessageBox);
                viewPanel.revalidate();
                viewPanel.repaint();

                // AI 답변 채팅(예시)
                JTextArea AiMessage = new JTextArea("서비스 준비 중입니다. \n\n < AI 답변 예시 >\n"
                        + "\n========================\n\n" + "1. 체크리스트 수행 비율 분석\n\n"
                        + "2. 사용자 공부 시간 분석\n\n" + "3. 월별 체크리스트 정리\n\n"
                        + "4. 학습 습관 기반 심층리포트\n\n" + "5. 대학생 생활/학습 루틴 추천\n\n");
                AiMessage.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
                AiMessage.setLineWrap(true);
                AiMessage.setEditable(false);
                AiMessage.setBackground(new Color(237, 248, 255));
                AiMessage.setSize(200,200);

                JPanel AiMessageBox = new JPanel(new FlowLayout(FlowLayout.LEFT)); // AI 답변 채팅 layout을 위한 패널
                AiMessageBox.setOpaque(false);
                AiMessageBox.add(AiMessage);

                //viewPanel에 챗 메시지 추가
                viewPanel.add(AiMessageBox);
                viewPanel.revalidate();
                viewPanel.repaint();

            }
            inputArea.setText("");

            // viewPanel 길이를 넘어갈 경우 스크롤 바 자동 생성
            SwingUtilities.invokeLater(() -> {
                viewScrollPane.getVerticalScrollBar().setValue(viewScrollPane.getVerticalScrollBar().getMaximum());
            });
        }
    }
}
