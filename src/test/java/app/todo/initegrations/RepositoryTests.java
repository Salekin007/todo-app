package app.todo.initegrations;

import app.todo.entity.TaskEntity;
import app.todo.entity.TodoEntity;
import app.todo.repository.TodoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositoryTests {

    @Autowired
    private TodoRepository todoRepository;

    private long todoId = 0L;

    @AfterAll
    @BeforeAll
    @DirtiesContext
    void cleanup() {
        todoRepository.deleteAll();
    }


    @Test
    @Order(1)
    public void createTodoWithNoTasks() {
        TodoEntity todo = new TodoEntity("A new todo");
        TodoEntity entity = todoRepository.save(todo);

        Assertions.assertNotNull(entity);

        this.todoId = entity.getId();
    }

    @Test
    @Order(2)
    public void createTodoWithATask() {
        TodoEntity todo = new TodoEntity();
        todo.setTitle("Todo new 2");

        TaskEntity task = new TaskEntity("Task A", todo);

        todo.getTasks().add(task);

        TodoEntity entity = todoRepository.save(todo);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(entity.getTasks().size(), 1);
    }

    @Test
    @Order(3)
    public void createTodoWithMultipleTasks() {
        TodoEntity todo = new TodoEntity();
        todo.setTitle("New Todo 3");


        todo.getTasks().addAll(generateTasks(todo));


        TodoEntity entity = todoRepository.save(todo);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(entity.getTasks().size(), 5);
    }


    @Test
    @Order(4)
    public void findAllTodosTest() {
        List<TodoEntity> todos = todoRepository.findAll();
        Assertions.assertEquals(todos.size(), 3);
    }

    //5: findTodoById
    @Test
    @Order(5)
    public void findTodoById() {
        Optional<TodoEntity> todoOptional = todoRepository.findById(todoId);
        Assertions.assertTrue(todoOptional.isPresent());
        Assertions.assertEquals(todoOptional.get().getTitle(), "A new todo");
        Assertions.assertEquals(todoOptional.get().getId(), todoId);
    }

    @Test
    @Order(6)
    public void findTodoByTitle() {
        List<TodoEntity> todos = todoRepository.findByTitleContainingIgnoreCase("new");
        Assertions.assertFalse(todos.isEmpty());
    }

    @Test
    @Order(7)
    public void updateTodoTitleTest() {
        Optional<TodoEntity> todoOptional = todoRepository.findById(todoId);
        Assertions.assertTrue(todoOptional.isPresent());

        TodoEntity todo = todoOptional.get();
        todo.setTitle("Updated Todo");
        todoRepository.save(todo);

        TodoEntity updatedTodo = todoRepository.findById(todoId).get();
        Assertions.assertEquals("Updated Todo", updatedTodo.getTitle());
    }

    @Test
    @Order(8)
    @Transactional
    // Transactional is needed to get support for Hibernate Lazy Loading
    // needed because of loading and modifying tasks which is a lazy list
    public void updateTodoAddNewTaskTest() {
        Optional<TodoEntity> todoOptional = todoRepository.findById(todoId);
        Assertions.assertTrue(todoOptional.isPresent());

        TodoEntity todoToUpdate = todoOptional.get();
        todoToUpdate.getTasks().add(new TaskEntity("A New task", todoToUpdate));
        todoRepository.save(todoToUpdate);

        TodoEntity updatedTodo = todoRepository.findById(todoId).get();
//        Assertions.assertEquals(1, updatedTodo.getTasks().size());
    }

    @Test
    @Order(9)
    public void deleteTodoById() {
        todoRepository.deleteById(todoId);
        Optional<TodoEntity> todoOptional = todoRepository.findById(todoId);
        Assertions.assertFalse(todoOptional.isPresent());
    }

    private List<TaskEntity> generateTasks(TodoEntity todo) {
        List<TaskEntity> tasks = new ArrayList<>();
        tasks.add(new TaskEntity("Task A", todo));
        tasks.add(new TaskEntity("Task B", todo));
        tasks.add(new TaskEntity("Task C", todo));
        tasks.add(new TaskEntity("Task D", todo));
        tasks.add(new TaskEntity("Task E", todo));
        return tasks;
    }
}
