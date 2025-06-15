// TodoItem.java 수정 - DocumentListener 문제 해결

package checklist;

import main.AppState;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;



public class TodoItem extends JPanel implements ActionListener {
    private JButton addButton;
    private JPanel todoListPanel;

    // 클래스 멤버 변수로 추가
    private PieChartPanel pieChartPanel = new PieChartPanel();

    // 메모 관련 필드 추가
    private JTextArea memoArea;
    private Timer autoSaveTimer;
    private DocumentListener memoDocumentListener; // DocumentListener 참조 저장
    private boolean isLoadingMemo = false; // 메모 로딩 중인지 확인하는 플래그

    // JSON 파일 경로
    private static final String DATA_FILE_PATH = "checklist/todo_data.json";

    // 할일 목록을 저장할 리스트
    private final List<TodoData> todoList;
    private final List<JCheckBox> checkBoxList;

    // 할일 목록 data 담김
    public static class TodoData {
        public String title;
        public boolean done;

        public TodoData(String title, boolean done) {
            this.title = title;
            this.done = done;
        }
    }

    // 생성자
    public TodoItem() {
        todoList = new ArrayList<>();
        checkBoxList = new ArrayList<>();

        setupUI();
        loadTodoData();
        refreshUI();
    }

    // UI 초기 설정
    private void setupUI() {
        setLayout(new BorderLayout());

        // 왼쪽 체크리스트 패널
        todoListPanel = new JPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(todoListPanel);

        // 상단에 추가 버튼
        addButton = new JButton("할 일 추가");
        addButton.addActionListener(this);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(addButton);

        // ✔️ 가운데 부분 : 체크리스트(왼쪽) + 파이차트(오른쪽)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        pieChartPanel.setPreferredSize(new Dimension(300, 180)); // 오른쪽 너비 조정
        centerPanel.add(pieChartPanel, BorderLayout.EAST); // 오른쪽에 PieChart

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }


    // 메모 영역을 외부에서 등록하는 메서드
    public void setMemoArea(JTextArea memoArea) {
        this.memoArea = memoArea;
        setupMemoListeners();
        loadAndSetMemo(); // 현재 날짜의 메모 로드
    }

    // 메모 영역에 이벤트 리스너 설정
    private void setupMemoListeners() {
        if (memoArea == null) return;

        // DocumentListener 생성 및 참조 저장
        memoDocumentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!isLoadingMemo) { // 로딩 중이 아닐 때만 자동저장
                    scheduleAutoSave();
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!isLoadingMemo) { // 로딩 중이 아닐 때만 자동저장
                    scheduleAutoSave();
                }
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!isLoadingMemo) { // 로딩 중이 아닐 때만 자동저장
                    scheduleAutoSave();
                }
            }

            private void scheduleAutoSave() {
                if (autoSaveTimer != null) {
                    autoSaveTimer.stop();
                }
                // 2초 후에 자동저장 실행
                autoSaveTimer = new Timer(2000, e -> {
                    saveMemoData();
                    System.out.println("메모 자동저장 완료");
                    autoSaveTimer = null;
                });
                autoSaveTimer.setRepeats(false);
                autoSaveTimer.start();
            }
        };

        // DocumentListener 등록
        memoArea.getDocument().addDocumentListener(memoDocumentListener);

        // 키 입력 이벤트를 사용한 저장
        memoArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Ctrl+S로 수동 저장
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    saveMemoData();
                    JOptionPane.showMessageDialog(TodoItem.this,
                            "메모가 저장되었습니다!", "저장 완료",
                            JOptionPane.INFORMATION_MESSAGE);
                    e.consume(); // 이벤트 소비
                }
            }
        });
    }

    // 메모 데이터만 저장하는 메서드
    private void saveMemoData() {
        if (memoArea == null) return;

        try {
            JSONObject allData = loadAllData();
            String dateKey = AppState.selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String memoText = memoArea.getText();

            JSONObject dateData;
            if (allData.containsKey(dateKey)) {
                dateData = (JSONObject) allData.get(dateKey);
            } else {
                dateData = new JSONObject();
                dateData.put("todos", new JSONArray());
            }

            dateData.put("memo", memoText);
            allData.put(dateKey, dateData);

            createDirectoryIfNotExists();
            try (FileWriter file = new FileWriter(DATA_FILE_PATH)) {
                file.write(allData.toJSONString());
                file.flush();
            }

        } catch (IOException e) {
            System.err.println("메모 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 메모 로드 및 설정 - 플래그를 사용한 방식으로 수정
    private void loadAndSetMemo() {
        if (memoArea == null) return;

        String memo = loadMemo();

        // 로딩 중임을 표시
        isLoadingMemo = true;

        // 메모 텍스트 설정
        memoArea.setText(memo);

        // 로딩 완료를 표시
        isLoadingMemo = false;

        System.out.println("메모 로드 완료: " + memo.length() + "글자, 날짜: " + AppState.selectedDate);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String input = JOptionPane.showInputDialog("무엇을 해야 하나요?:");

            if (input != null && !input.trim().isEmpty()) {
                System.out.println("입력된 이름: " + input);
                addTodo(input, false);
            } else {
                System.out.println("사용자가 입력 취소");
            }
        }
        saveMemoData();     // 모든 이벤트 발생시 할일 내역 저장
    }

    public void addTodo(String title, boolean done) {
        TodoData newTodo = new TodoData(title, done);
        todoList.add(newTodo);

        JCheckBox checkBox = new JCheckBox(title, done);
        checkBox.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        checkBox.setBackground(Color.WHITE);

        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                newTodo.done = checkBox.isSelected();
                saveTodoData();
            }
        });

        checkBoxList.add(checkBox);
        refreshUI();
        saveTodoData();
    }

    private void refreshUI() {
        todoListPanel.removeAll();

        for (int i = 0; i < todoList.size() && i < checkBoxList.size(); i++) {
            TodoData todo = todoList.get(i);
            JCheckBox checkBox = checkBoxList.get(i);

            checkBox.setText(todo.title);
            checkBox.setSelected(todo.done);

            // 할일 체크 선택/해제 시 todo.done 업데이트 및 차트 갱신
            checkBox.addItemListener(e -> {
                todo.done = checkBox.isSelected();
                updatePieChart(); // 그래프 업데이트
                saveTodoData();   // 할일 저장
            });

            JPanel todoPanel = new JPanel(new BorderLayout());
            todoPanel.setBackground(Color.WHITE);
            todoPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            todoPanel.add(checkBox, BorderLayout.WEST);

            todoListPanel.add(todoPanel);
        }

        todoListPanel.add(Box.createVerticalGlue());

        refreshMemoArea();

        revalidate();
        repaint();

        updatePieChart(); //  전체 UI 갱신 후 차트도 업데이트
    }


    private void updatePieChart() {
        int total = todoList.size();
        int completed = 0;

        for (TodoData data : todoList) {
            if (data.done) {
                completed++;
            }
        }

        pieChartPanel.updateData(completed, total);
    }



    // 메모 영역을 새로고침하는 별도 메서드
    private void refreshMemoArea() {
        if (memoArea != null) {
            // 현재 날짜에 해당하는 메모 로드
            String memo = loadMemo();

            // 로딩 중임을 표시 (자동저장 방지)
            isLoadingMemo = true;

            // 메모 텍스트 설정
            memoArea.setText(memo);

            // 로딩 완료 표시
            isLoadingMemo = false;

            // 메모 영역 새로고침
            memoArea.revalidate();
            memoArea.repaint();

            System.out.println("메모 영역 새로고침 완료: " + AppState.selectedDate);
        }
    }

    private void saveTodoData() {
        try {
            JSONObject allData = loadAllData();
            String dateKey = AppState.selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            JSONObject dateData = new JSONObject();

            JSONArray todoArray = new JSONArray();
            for (TodoData todo : todoList) {
                JSONObject todoObj = new JSONObject();
                todoObj.put("title", todo.title);
                todoObj.put("done", todo.done);
                todoArray.add(todoObj);
            }

            dateData.put("todos", todoArray);

            // 기존 메모 데이터 보존
            if (allData.containsKey(dateKey)) {
                JSONObject existingData = (JSONObject) allData.get(dateKey);
                String memo = (String) existingData.get("memo");
                dateData.put("memo", memo != null ? memo : "");
            } else {
                dateData.put("memo", "");
            }

            allData.put(dateKey, dateData);

            createDirectoryIfNotExists();
            try (FileWriter file = new FileWriter(DATA_FILE_PATH)) {
                file.write(allData.toJSONString());
                file.flush();
            }

            System.out.println("할일 데이터가 저장되었습니다: " + dateKey);

        } catch (IOException e) {
            System.err.println("할일 데이터 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTodoData() {
        try {
            JSONObject allData = loadAllData();
            String dateKey = AppState.selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

            todoList.clear();
            checkBoxList.clear();

            if (allData.containsKey(dateKey)) {
                JSONObject dateData = (JSONObject) allData.get(dateKey);
                JSONArray todoArray = (JSONArray) dateData.get("todos");

                if (todoArray != null) {
                    for (Object obj : todoArray) {
                        JSONObject todoObj = (JSONObject) obj;
                        String title = (String) todoObj.get("title");
                        Boolean done = (Boolean) todoObj.get("done");

                        TodoData todo = new TodoData(title, done != null ? done : false);
                        todoList.add(todo);

                        JCheckBox checkBox = new JCheckBox(title, todo.done);
                        checkBox.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
                        checkBox.setBackground(Color.WHITE);

                        checkBoxList.add(checkBox);
                    }

                    System.out.println("할일 데이터가 로드되었습니다: " + dateKey + " (총 " + todoList.size() + "개)");
                }
            }
        } catch (Exception e) {
            System.err.println("할일 데이터 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JSONObject loadAllData() {
        JSONObject allData = new JSONObject();

        try {
            File file = new File(DATA_FILE_PATH);
            if (file.exists()) {
                JSONParser parser = new JSONParser();
                try (FileReader reader = new FileReader(file)) {
                    Object obj = parser.parse(reader);
                    if (obj instanceof JSONObject) {
                        allData = (JSONObject) obj;
                    }
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("JSON 파일 로드 중 오류 발생: " + e.getMessage());
        }

        return allData;
    }

    private void createDirectoryIfNotExists() {
        File file = new File(DATA_FILE_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    // 날짜가 변경되었을 때 호출할 메서드
    public void onDateChanged(LocalDate oldDate, LocalDate newDate) {
        System.out.println("날짜 변경: " + oldDate + " -> " + newDate);

        // 이전 날짜의 메모 저장 (현재 텍스트 영역의 내용)
        if (memoArea != null && !memoArea.getText().isEmpty()) {
            // 임시로 선택 날짜를 이전 날짜로 설정해서 저장
            LocalDate tempDate = AppState.selectedDate;
            AppState.selectedDate = oldDate;
            saveMemoData();
            AppState.selectedDate = tempDate; // 다시 새 날짜로 복원
            System.out.println("이전 날짜(" + oldDate + ") 메모 저장 완료");
        }

        // 새로운 날짜의 데이터 로드
        loadTodoData();

        //날짜 바뀌면 그래프도 업데이트
        updatePieChart();

        // UI 새로고침 (메모 포함)
        refreshUI();
    }

    public String loadMemo() {
        try {
            JSONObject allData = loadAllData();
            String dateKey = AppState.selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

            if (allData.containsKey(dateKey)) {
                JSONObject dateData = (JSONObject) allData.get(dateKey);
                String memo = (String) dateData.get("memo");
                return memo != null ? memo : "";
            }
        } catch (Exception e) {
            System.err.println("메모 로드 중 오류 발생: " + e.getMessage());
        }

        return "";
    }
}
