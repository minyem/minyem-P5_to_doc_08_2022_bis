package com.cleanup.todoc.repository;

import android.content.Context;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.cleanup.todoc.dao.ProjectDAO;
import com.cleanup.todoc.dao.TaskDAO;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import java.util.List;

import utils.AppDatabase;

public class ProjectRepository {


    private ProjectDAO db;

    public ProjectRepository(Context context) {
         db = AppDatabase.getDatabase(context).projectDAO();
    }

    public List<Project> getALLProjects() {
        return db.getAllProjects();
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertProject(Project project) {
         db.insertProject(project);
    }

    public void insertAllProject(List<Project> tasks) {
        for (Project project : tasks) {
            db.insertProject(project);
        }
    }
    public int updateProject(Project project) {
       return db.updateProject(project);
    }
    public void deleteProject(Project project) {
        db.deleteProject(project);
    }
    public Project getProjectById(int id) { return db.getProjectById(id); }
}
