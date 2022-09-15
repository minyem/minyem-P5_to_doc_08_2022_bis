package com.cleanup.todoc;




import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;

import android.content.Context;


import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.cleanup.todoc.dao.ProjectDAO;
import com.cleanup.todoc.dao.TaskDAO;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.repository.TaskRepository;
import com.cleanup.todoc.ui.MainActivity;


import org.junit.Before;


import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utils.AppDatabase;

@RunWith(AndroidJUnit4.class)
public class TestTaskDao {

    private List<Task> allTasks = new ArrayList<>();

    private TaskRepository taskRepository = null;

    Project project = new Project(1, "Projet Tartampion", 0xFFEADAD1);
    Project project1 = new Project(2, "Projet Lucidia", 0xFFEADAD1);
    Project project2 =new Project(3L, "Projet Circus", 0xFFA3CED2);

    Task task1 = new Task(1,1,"Task 1", System.currentTimeMillis());
    Task task2 = new Task(2,2,"Task 2", System.currentTimeMillis());
    Task task3 = new Task(3,1,"Task 3", System.currentTimeMillis());



    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void TaskRepository() {
        taskRepository = new TaskRepository(mActivityRule.getActivity());

    }

    @Test
    public void  baseVideTaskTest(){

        taskRepository.insertAllTask(allTasks);
        assertTrue(allTasks.isEmpty());
    }

    @Test
    public void  insertTaskTest(){

        allTasks = taskRepository.getAllTasks();
        assertEquals(3, allTasks.size());
    }

    @Test
    public void  deleteTaskTest(){
        taskRepository.deleteTask(task1);
        allTasks=taskRepository.getAllTasks();
        assertEquals(2, allTasks.size());
    }

    @Test
    public void  updateTaskTest(){
         task1=taskRepository.getTaskById((int) task2.getId());
        assertEquals("Task 2",task1.getName());

         task1.setName("Task 1");
         taskRepository.updateTask(task1);
         task1=taskRepository.getTaskById((int) task1.getId());
        assertEquals("Task 1",task1.getName());
    }
    

}
