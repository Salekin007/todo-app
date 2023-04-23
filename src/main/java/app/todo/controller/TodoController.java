package app.todo.controller;

import app.todo.dto.ApiError;
import app.todo.dto.TaskRequest;
import app.todo.dto.TodoRequest;
import app.todo.dto.response.TaskResponse;
import app.todo.dto.response.TodoResponse;
import app.todo.entity.TaskEntity;
import app.todo.entity.TodoEntity;
import app.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoRepository todoRepository;

    @GetMapping("/todos")
    public ResponseEntity<?> fetchAllTodo() {
        List<TodoEntity> todoEntities = todoRepository.findAll();
        return ResponseEntity.ok(todoEntities);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<?> fetchTodoById(@PathVariable long id) {
        Optional<TodoEntity> todoOpt = todoRepository.findById(id);
        // TODO: move to service
        if (todoOpt.isPresent()) {
            return ResponseEntity.ok(
                    toResponse(todoOpt.get())
            );
        }
        return ResponseEntity.badRequest()
                .body(new ApiError("No value present"));
    }

    @PostMapping("/todos")
    public ResponseEntity<?> createNewTodo(@RequestBody TodoRequest request) {
        // mapped from TodoRequest dto
        TodoEntity newEntity = toEntity(request);
        // save entity to DB
        TodoEntity todoCreated = todoRepository.save(newEntity);
        // convert entity to response dto
        TodoResponse todoResponse = toResponse(todoCreated);
        // return the response object
        return ResponseEntity.ok(todoResponse);
    }


    // Mapping function -> move to TodoMapper.java class
    private TodoEntity toEntity(TodoRequest request) {
        TodoEntity newEntity = new TodoEntity(request.getTitle());
        for (TaskRequest task: request.getTasks()) {
            newEntity.getTasks().add(
                    new TaskEntity(task.getTitle(), newEntity)
            );
        }
        return newEntity;
    }

    // Mapping function -> move to TodoMapper.java class
    private TodoResponse toResponse(TodoEntity todoEntity) {
        List<TaskResponse> taskResponseList = todoEntity.getTasks()
                .stream()
                .map(
                        taskEntity -> new TaskResponse(taskEntity.getId(), taskEntity.getTitle())
                ).toList();

        return new TodoResponse(
                todoEntity.getId(),
                todoEntity.getTitle(),
                taskResponseList
        );
    }
}
