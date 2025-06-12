package main;

import java.time.LocalDate;
import java.util.*;
import checklist.TodoItem;

/**
 * 앱 전체에서 공통으로 사용하는 상태(데이터)를 관리하는 클래스입니다.
 * 싱글톤(Singleton) 패턴으로 구현되어, 프로그램 전체에서 하나만 생성됩니다.
 */
public class AppState {
    // 싱글톤 인스턴스
    private static AppState instance = null;

    // 현재 달력에서 선택된 날짜
    public LocalDate selectedDate;

    // 날짜별 체크리스트 목록 저장 (예: 2025-06-09 → [할일1, 할일2, ...])
    public Map<LocalDate, List<TodoItem>> todoMap;

    // 날짜별 메모 내용 저장 (예: 2025-06-09 → "오늘 회의함")
    public Map<LocalDate, String> memoMap;

    /**
     * 생성자 - 외부에서 호출하지 못하도록 private 처리.
     * 내부에서 초기값들을 설정합니다.
     */
    private AppState() {
        selectedDate = LocalDate.now();       // 기본 날짜: 오늘
        todoMap = new HashMap<>();            // 체크리스트 초기화
        memoMap = new HashMap<>();            // 메모장 초기화
    }

    /**
     * AppState의 유일한 인스턴스를 반환합니다.
     * 없으면 새로 생성해서 반환합니다.
     */
    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }
}