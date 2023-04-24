package app.todo.controller;

import app.todo.dto.ApiError;
import app.todo.dto.TaskRequest;
import app.todo.dto.TodoRequest;
import app.todo.dto.request.TaskBulkUpdateRequest;
import app.todo.dto.request.TodoUpdateRequest;
import app.todo.dto.response.TaskResponse;
import app.todo.dto.response.TodoResponse;
import app.todo.entity.TaskEntity;
import app.todo.entity.TodoEntity;
import app.todo.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @PatchMapping("/todos/{id}")
    public ResponseEntity<TodoResponse> updateTodoTitle(@PathVariable Long id, @RequestBody TodoUpdateRequest request) {
        TodoEntity todoEntity = todoRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found"));

        // just updating the title here, nothing more than that
        todoEntity.setTitle(request.getTitle());
        TodoEntity updatedTodoEntity = todoRepository.save(todoEntity);

        return ResponseEntity.ok(toResponse(updatedTodoEntity));
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @RequestBody TodoUpdateRequest request) {
        TodoEntity todoEntity = todoRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found"));

        TodoEntity todoEntityToUpdate = toUpdatedEntity(request, todoEntity);

        TodoEntity updatedTodoEntity = todoRepository.save(todoEntityToUpdate);

        return ResponseEntity.ok(toResponse(updatedTodoEntity));
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

    private TodoEntity toUpdatedEntity(TodoUpdateRequest request, TodoEntity todoEntity) {
        todoEntity.setTitle(request.getTitle());

        for (TaskBulkUpdateRequest taskUpdateRequest : request.getTasks()) {
            Optional<TaskEntity> taskEntityOptional = todoEntity.getTasks()
                    .stream()
                    .filter(taskEntity -> taskEntity.getId() == taskUpdateRequest.getId())
                    .findFirst();
            if (taskEntityOptional.isPresent()) {
                TaskEntity taskEntityToUpdate = taskEntityOptional.get();
                taskEntityToUpdate.setTitle(taskUpdateRequest.getTitle());
                taskEntityToUpdate.setStatus(taskUpdateRequest.getStatus());
            }
        }
        return todoEntity;
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
