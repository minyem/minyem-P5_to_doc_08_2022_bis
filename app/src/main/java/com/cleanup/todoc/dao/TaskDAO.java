package com.cleanup.todoc.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.cleanup.todoc.model.Task;

import java.util.List;

    @Dao
    public interface TaskDAO {

        @Insert
        void insertTask(Task task);

        @Query("Select * from Task")
        List<Task> getAllTasks();

        @Query("Select * from Task WHERE id = :myId ")
        Task getTaskById(int myId);

        @Update
        int updateTask(Task task);

        @Delete
        void deleteTask(Task task);

}
