package checklist;

/**
 * 체크리스트의 개별 할 일을 표현하는 클래스입니다.
 */
public class TodoItem {
    // 할 일 제목
    public String title;

    // 완료 여부 (true면 완료됨)
    public boolean done;

    // 생성자
    public TodoItem(String title, boolean done) {
        this.title = title;
        this.done = done;
    }
}

