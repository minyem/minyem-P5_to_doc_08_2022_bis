package utils;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cleanup.todoc.dao.ProjectDAO;
import com.cleanup.todoc.dao.TaskDAO;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;



@Database(entities = {Task.class, Project.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

     public abstract TaskDAO taskDAO();
     public abstract ProjectDAO projectDAO();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class,
                                    "database").build();
                }
            }
        }
        return INSTANCE;
    }
}