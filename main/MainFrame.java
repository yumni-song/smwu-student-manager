package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import calendar.CalendarPanel;
import checklist.TodoItem;
import chatbot.ChatbotPanel;

/**
 * 기존 깃허브 시계 UI와 레이아웃 스타일을 참고한 MainFrame 구현
 */
public class MainFrame extends JFrame {

    // 시작 화면 컴포넌트
    private JPanel startPanel;
    private JButton startButton;
    private JLabel titleLabel;
    private JTextArea descriptionArea;

    // 메인 화면 컴포넌트
    private ClockPanel clockPanel;       // 기존 깃허브 시계 컴포넌트
    private ChatbotPanel chatbotPanel;   // 챗봇 패널 참조

    private JPanel calendarPanelContainer;      // 중앙 위 캘린더 화면 (기존 upperEmptyPanel)
    private JTabbedPane lowerTabbedPane; // 중앙 아래 탭 2개
    private TodoItem todoItem;           // 체크리스트 컴포넌트
    private CalendarPanel calendarPanel; // 달력 패널 참조

    private JSplitPane centerSplitPane;  // 중앙 영역 수직 분할

    private CardLayout mainCardLayout;
    private JPanel mainPanel;            // 전체 화면 카드 레이아웃

    // 색상 통일
    private final Color bgColor = new Color(214, 240, 255);
    private final Color borderColor = new Color(144, 198, 224);
    private final Color textColor = new Color(48, 80, 96);

    public MainFrame() {
        setTitle("대학생 관리 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 850);
        setLocationRelativeTo(null);

        mainCardLayout = new CardLayout();
        mainPanel = new JPanel(mainCardLayout);
        mainPanel.setBackground(bgColor);

        createStartPanel();
        createMainPanel();

        mainPanel.add(startPanel, "start");
        mainPanel.add(createMainScreenPanel(), "main");

        add(mainPanel);
        mainCardLayout.show(mainPanel, "start");
    }

    private void createStartPanel() {
        startPanel = new JPanel(new BorderLayout(10, 10));
        startPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        startPanel.setBackground(bgColor);

        titleLabel = new JLabel("대학생 관리 시스템", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        titleLabel.setForeground(textColor);
        titleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        descriptionArea = new JTextArea(
                "이 프로그램은 대학생들의 일정, 메모, 체크리스트를 관리하는 시스템입니다.\n" +
                        "효율적인 시간 관리와 목표 달성을 도와줍니다."
        );
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        descriptionArea.setForeground(textColor);
        descriptionArea.setBackground(bgColor);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new EmptyBorder(0, 10, 0, 10));
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 이미지 삽입
        JLabel imageLabel = new JLabel();
        try {
            ImageIcon imageIcon = new ImageIcon(getClass().getResource("/images/noonsong.png"));
            Image image = imageIcon.getImage().getScaledInstance(381, 426, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            // 이미지 로드 실패 시 텍스트로 대체
            imageLabel.setText("이미지를 불러올 수 없습니다");
            imageLabel.setForeground(textColor);
        }
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 시작 버튼
        startButton = new JButton("시작하기");
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        startButton.setPreferredSize(new Dimension(150, 50));
        startButton.addActionListener(e -> mainCardLayout.show(mainPanel, "main"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bgColor);
        buttonPanel.add(startButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 세로로 배치
        Box centerBox = Box.createVerticalBox();
        centerBox.setBackground(bgColor);
        centerBox.add(titleLabel);
        centerBox.add(Box.createVerticalStrut(10));
        centerBox.add(descriptionArea);
        centerBox.add(Box.createVerticalStrut(10));
        centerBox.add(imageLabel);
        centerBox.add(Box.createVerticalStrut(20));

        startPanel.add(centerBox, BorderLayout.CENTER);
        startPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createMainPanel() {
        // TodoItem 초기화
        todoItem = new TodoItem();

        // 메모장 패널 생성
        JPanel memoPanel = new JPanel(new BorderLayout());
        memoPanel.setBackground(Color.WHITE);
        JTextArea memoArea = new JTextArea();
        memoArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        memoArea.setLineWrap(true);
        memoArea.setWrapStyleWord(true);
        JScrollPane memoScrollPane = new JScrollPane(memoArea);
        memoPanel.add(memoScrollPane, BorderLayout.CENTER);

        // TodoItem에 메모 영역 등록
        todoItem.setMemoArea(memoArea);

        // 중앙 아래 탭 생성
        lowerTabbedPane = new JTabbedPane();
        lowerTabbedPane.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        lowerTabbedPane.addTab("체크리스트", todoItem);  // TodoItem을 체크리스트 탭에 추가
        lowerTabbedPane.addTab("메모장", memoPanel);
        lowerTabbedPane.setBackground(new Color(245, 240, 220));
        lowerTabbedPane.setForeground(textColor);

        // 탭 변경 시에도 저장 (TodoItem에서 자동 처리됨)
        lowerTabbedPane.addChangeListener(e -> {
            // TodoItem이 알아서 처리하므로 별도 코드 불필요
        });

        // 시계 패널
        clockPanel = new ClockPanel();
        clockPanel.setPreferredSize(new Dimension(250, 250));
        clockPanel.setBorder(BorderFactory.createEmptyBorder());
        clockPanel.setBackground(bgColor);

        // 챗봇 패널
        chatbotPanel = new ChatbotPanel();
        chatbotPanel.setPreferredSize(new Dimension(300, 600));
        chatbotPanel.setBackground(bgColor);
        chatbotPanel.setBorder(BorderFactory.createLineBorder(borderColor, 2));

        // 중앙 캘린더 영역
        calendarPanelContainer = new JPanel();
        calendarPanelContainer.setOpaque(false);
        calendarPanelContainer.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        calendarPanelContainer.setBackground(new Color(245, 240, 220));
        calendarPanelContainer.setLayout(new BorderLayout());

        // CalendarPanel 생성 및 날짜 변경 이벤트 처리
        calendarPanel = new CalendarPanel(lowerTabbedPane) {
            @Override
            protected void onDateChanged(LocalDate oldDate, LocalDate newDate) {
                // 날짜가 변경되면 TodoItem에 알림
                if (todoItem != null) {
                    todoItem.onDateChanged(oldDate, newDate);
                }
            }
        };
        calendarPanel.setOpaque(false);
        calendarPanelContainer.add(calendarPanel, BorderLayout.CENTER);

        // 중앙 4:3 분할
        centerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, calendarPanelContainer, lowerTabbedPane);
        centerSplitPane.setResizeWeight(0.57);
        centerSplitPane.setDividerSize(6);
        centerSplitPane.setOneTouchExpandable(true);
        centerSplitPane.setBorder(null);
        centerSplitPane.setBackground(bgColor);
    }

    private JPanel createMainScreenPanel() {
        JPanel mainScreen = new JPanel(new BorderLayout(15, 15));
        mainScreen.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainScreen.setBackground(bgColor);

        // 챗봇 + 프로그램 제목 아래 붙이기
        Box rightBox = Box.createVerticalBox();
        rightBox.setBackground(bgColor);

        rightBox.add(chatbotPanel);
        rightBox.add(Box.createVerticalStrut(15));

        JLabel programTitle = new JLabel("대학생 관리 시스템", SwingConstants.CENTER);
        programTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        programTitle.setForeground(textColor);
        programTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightBox.add(programTitle);

        mainScreen.add(clockPanel, BorderLayout.WEST);
        mainScreen.add(centerSplitPane, BorderLayout.CENTER);
        mainScreen.add(rightBox, BorderLayout.EAST);

        return mainScreen;
    }
}

/**
 * 진서님 ui참고해서 ClockPanel 구현
 */
class ClockPanel extends JPanel {
    private JLabel dateLabel;
    private JLabel timeLabel;

    public ClockPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(214, 240, 255));

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateLabel.setForeground(new Color(48, 80, 96));

        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 48));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setForeground(new Color(48, 80, 96));

        add(Box.createVerticalStrut(200));
        add(dateLabel);
        add(Box.createVerticalStrut(10));
        add(timeLabel);
        add(Box.createVerticalGlue());

        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();

        updateTime();
    }

    private void updateTime() {
        java.time.LocalDate date = java.time.LocalDate.now();
        java.time.LocalTime time = java.time.LocalTime.now().withNano(0);

        dateLabel.setText(date.toString());
        timeLabel.setText(time.toString());
    }
}