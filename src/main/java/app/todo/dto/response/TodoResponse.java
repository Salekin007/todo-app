package app.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TodoResponse {
    private long todoId;
    private String title;
    private List<TaskResponse> tasks;
}
