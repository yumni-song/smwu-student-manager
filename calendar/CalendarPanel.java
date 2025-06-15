package calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import main.AppState;

/**
* CalendarPanel 클래스는 달력 UI를 구성
* 사용자가 날짜를 클릭하면 선택한 날짜는 APPState에 저장됨
* 그리고 하단의 탭(체크리스트 또는 메모)이 전환되도록 합니다
 */



public abstract class CalendarPanel extends JPanel{
    private final JLabel monthYearLabel; //현재 년/월 표시
    private final JLabel selectedDateLabel; //선택된 날짜 표시
    private final JPanel datePanel; //날짜 버튼들이 들어가 패널
    private int currentYear, currentMonth; //현재 보고 있는 년/월
    private final JTabbedPane tabbedPane;//아래 탭

    // CalendarPanel.java 안에
    private List<Consumer<LocalDate>> dateListeners = new ArrayList<>();

    public void addDateClickListener(Consumer<LocalDate> listener) {
        dateListeners.add(listener);
    }

    // 날짜 클릭 시 호출
    private void notifyDateClicked(LocalDate date) {
        for (Consumer<LocalDate> listener : dateListeners) {
            listener.accept(date);
        }
    }



    //생성자
    public CalendarPanel(JTabbedPane tabbedPane){
        this.tabbedPane = tabbedPane;
        setLayout(new BorderLayout());
        setBackground(Color.white);

        //초기 날짜 설정
        currentYear = AppState.selectedDate.getYear();
        currentMonth = AppState.selectedDate.getMonthValue();


        //선택 날짜 표시 + Today 버튼
        selectedDateLabel = new JLabel(AppState.selectedDate.toString());

        //Today Button : 오늘 날짜로 이동
        JButton todayBtn = new JButton("Today");
        todayBtn.addActionListener(e ->{
            LocalDate oldDate = AppState.selectedDate;
            AppState.selectedDate = LocalDate.now();
            currentYear = AppState.selectedDate.getYear();
            currentMonth = AppState.selectedDate.getMonthValue();
            updateCalendar();
            selectedDateLabel.setText(AppState.selectedDate.toString());

            // 날짜 변경 콜백 호출
            onDateChanged(oldDate, AppState.selectedDate);
        });


        //상단 바 (년도/월 변경)
        JPanel topPanel = new JPanel();
        JButton prevYear = new JButton("<<");
        JButton prevMonth = new JButton("<");
        JButton nextMonth = new JButton(">");
        JButton nextYear = new JButton(">>");

        monthYearLabel = new JLabel("", SwingConstants.CENTER);

        prevYear.addActionListener(e -> { currentYear--; updateCalendar(); });
        prevMonth.addActionListener(e -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            updateCalendar();
        });

        nextMonth.addActionListener(e -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            updateCalendar();
        });

        nextYear.addActionListener(e -> { currentYear++; updateCalendar(); });

        //상단 버튼/라벨 배치
        topPanel.add(todayBtn);
        topPanel.add(selectedDateLabel);
        topPanel.add(prevYear);
        topPanel.add(prevMonth);
        topPanel.add(monthYearLabel);
        topPanel.add(nextMonth);
        topPanel.add(nextYear);

        datePanel = new JPanel(new GridLayout(0, 7));
        add(topPanel, BorderLayout.NORTH); //상단에 topPanel
        add(datePanel, BorderLayout.CENTER); //가운데 날짜 panel

        updateCalendar();
    }

    //달력 그리기
    private void updateCalendar() {
        monthYearLabel.setText(currentYear + " / " + currentMonth);
        datePanel.removeAll();

        //요일 헤더 추가
        String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setOpaque(true);
            if (day.equals("SUN")) label.setForeground(Color.RED);
            else if (day.equals("SAT")) label.setForeground(Color.BLUE);
            datePanel.add(label);
        }

        //1일이 시작되는 요일 기준 빈칸 추가
        LocalDate firstDay = LocalDate.of(currentYear, currentMonth, 1);
        int offset = firstDay.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < offset; i++) {
            datePanel.add(new JLabel(""));
        }

        //날짜 버튼 생성
        int daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(currentYear, currentMonth, day);
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            dayLabel.setOpaque(true);

            // 선택한 날짜 분홍 배경
            if (date.equals(AppState.selectedDate)) {
                dayLabel.setBackground(Color.PINK);
            } else {
                dayLabel.setBackground(Color.WHITE);
            }

            //클릭 시 이벤트 처리
            dayLabel.addMouseListener(new MouseAdapter() {
                

                //날짜 클릭하면 배경색 갱신
                @Override
                public void mouseClicked(MouseEvent e) {
                    LocalDate oldDate = AppState.selectedDate;
                    AppState.selectedDate = date;
                    selectedDateLabel.setText(date.toString());
                    updateCalendar();

                    //체크리스트 or 메모 탭 연결
                    if(tabbedPane != null){
                        tabbedPane.setSelectedIndex(0);
                    }

                    // 날짜 변경 콜백 호출
                    onDateChanged(oldDate, AppState.selectedDate);
                }

                //마우스 올릴 시 파랑색
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!date.equals(AppState.selectedDate))
                        dayLabel.setBackground(new Color(220, 230, 250));
                }

                //마우스 벗어나면 다시 하얀색으로 돌아오게
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!date.equals(AppState.selectedDate))
                        dayLabel.setBackground(Color.WHITE);
                }
            });

            datePanel.add(dayLabel);
        }

        revalidate();
        repaint();
    }

    protected abstract void onDateChanged(LocalDate oldDate, LocalDate newDate);
}
