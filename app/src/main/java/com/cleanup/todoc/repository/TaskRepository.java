package com.cleanup.todoc.repository;

import android.content.Context;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.cleanup.todoc.dao.TaskDAO;
import com.cleanup.todoc.model.Task;

import java.util.List;

import utils.AppDatabase;

public class TaskRepository {


    private TaskDAO db;

    public TaskRepository(Context context) {
         db = AppDatabase.getDatabase(context).taskDAO();
    }

    public List<Task> getAllTasks() {
        return db.getAllTasks();
    }
    public Task getTaskById(int id) {
        return db.getTaskById(id);
    }

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void insertTask(Task task) {
         db.insertTask(task);
    }
    public void cleanTasks(List<Task> tasks) {
        for (Task task : tasks) {
            db.deleteTask(task);
        }
    }

    public void insertAllTask(List<Task> tasks) {
        for (Task task : tasks) {
            db.insertTask(task);
        }
    }
    public int updateTask(Task task) {
       return db.updateTask(task);
    }
    public void deleteTask(Task task) {
        db.deleteTask(task);
    }
}
