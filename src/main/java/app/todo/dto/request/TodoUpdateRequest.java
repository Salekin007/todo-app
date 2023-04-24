package app.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TodoUpdateRequest {
    private String title;
    private List<TaskBulkUpdateRequest> tasks = new ArrayList<>();
}
