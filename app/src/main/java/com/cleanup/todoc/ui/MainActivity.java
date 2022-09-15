package com.cleanup.todoc.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleanup.todoc.R;
import com.cleanup.todoc.repository.ProjectRepository;
import com.cleanup.todoc.repository.TaskRepository;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * <p>Home activity of the application which is displayed when the user opens the app.</p>
 * <p>Displays the list of tasks.</p>
 *
 * @author Gaëtan HERFRAY
 */
public class MainActivity extends AppCompatActivity implements TasksAdapter.DeleteTaskListener {
    /**
     * List of all projects available in the application
     */
    private List<Project> allProjects;

    /**
     * List of all current tasks of the application
     */

    // lecture BDD et initialisation de la liste avec tous les éléments de la BDD


    // private Helper help   = new Helper(this);
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private ArrayList<Task> tasks;
    private  TasksAdapter adapter ;





    /**-
     * The adapter which handles the list of tasks
     */


    /**
     * The sort method to be used to display tasks
     */
    @NonNull
    private SortMethod sortMethod = SortMethod.NONE;

    /**
     * Dialog to create a new task
     */
    @Nullable
    public AlertDialog dialog = null;

    /**
     * EditText that allows user to set the name of a task
     */
    @Nullable
    private EditText dialogEditText = null;

    /**
     * Spinner that allows the user to associate a project to a task
     */
    @Nullable
    private Spinner dialogSpinner = null;

    /**
     * The RecyclerView which displays the list of tasks
     */
    // Suppress warning is safe because variable is initialized in onCreate
    @SuppressWarnings("NullableProblems")
    @NonNull
    private RecyclerView listTasks;



    /**
     * The TextView displaying the empty state
     */
    // Suppress warning is safe because variable is initialized in onCreate
    @SuppressWarnings("NullableProblems")
    @NonNull
    private TextView lblNoTasks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listTasks = findViewById(R.id.list_tasks);
        lblNoTasks = findViewById(R.id.lbl_no_task);

        taskRepository = new TaskRepository(this);
        projectRepository = new ProjectRepository(this);

        listTasks.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if(taskRepository.getAllTasks().isEmpty())
                {
                    insertRandomTasks();
                }

                tasks = (ArrayList<Task>) taskRepository.getAllTasks();
                adapter  = new TasksAdapter(tasks, MainActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listTasks.setAdapter(adapter);
                    }
                });

                allProjects = projectRepository.getALLProjects();
                updateTasks();
            }
        });

        findViewById(R.id.fab_add_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTaskDialog();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        tasks = (ArrayList<Task>) taskRepository.getAllTasks();
                        updateTasks();
                    }
                });

            }
        }
    }


    private void insertRandomTasks() {

        List<Task> tasks = new ArrayList<>();
        Project project = new Project(1, "Projet Tartampion", 0xFFEADAD1);
        Project project1 = new Project(2, "Projet Lucidia", 0xFFBB292C);
        Project project2 =new Project(3L, "Projet Circus", 0xFFA3CED2);

        projectRepository.insertProject(project);
        projectRepository.insertProject(project1);
        projectRepository.insertProject(project2);

        Task task1 = new Task(1,1,"Task 1", System.currentTimeMillis());
        Task task2 = new Task(2,2,"Task 2", System.currentTimeMillis());
        Task task3 = new Task(3,3l,"Task 3", System.currentTimeMillis());

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);

        taskRepository.insertAllTask(tasks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_alphabetical) {
            sortMethod = SortMethod.ALPHABETICAL;
        } else if (id == R.id.filter_alphabetical_inverted) {
            sortMethod = SortMethod.ALPHABETICAL_INVERTED;
        } else if (id == R.id.filter_oldest_first) {
            sortMethod = SortMethod.OLD_FIRST;
        } else if (id == R.id.filter_recent_first) {
            sortMethod = SortMethod.RECENT_FIRST;
        }

        updateTasks();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteTask(final Task task) {
        tasks.remove(task);
        updateTasks();

        // Delete task de la BDD
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                taskRepository.deleteTask(task);
            }
        });
    }

    /**
     * Called when the user clicks on the positive button of the Create Task Dialog.
     *
     * @param dialogInterface the current displayed dialog
     */
    private void onPositiveButtonClick(DialogInterface dialogInterface) {
        // If dialog is open
        if (dialogEditText != null && dialogSpinner != null) {
            // Get the name of the task
            String taskName = dialogEditText.getText().toString();

            // Get the selected project to be associated to the task
            Project taskProject = null;
            if (dialogSpinner.getSelectedItem() instanceof Project) {
                taskProject = (Project) dialogSpinner.getSelectedItem();
            }

            // If a name has not been set
            if (taskName.trim().isEmpty()) {
                dialogEditText.setError(getString(R.string.empty_task_name));
            }
            // If both project and name of the task have been set
            else if (taskProject != null) {
                // TODO: Replace this by id of persisted task
                long id = (long) (Math.random() * 50000);


                final Task task = new Task(
                        id,
                        taskProject.getId(),
                        taskName,
                        new Date().getTime()
                );
                addTask(task);

                // Delete task de la BDD



                dialogInterface.dismiss();
            }
            // If name has been set, but project has not been set (this should never occur)
            else{
                dialogInterface.dismiss();
            }
        }
        // If dialog is already closed
        else {
            dialogInterface.dismiss();
        }
    }

    /**
     * Shows the Dialog for adding a Task
     */
    private void showAddTaskDialog() {
        final AlertDialog dialog = getAddTaskDialog();

        dialog.show();

        dialogEditText = dialog.findViewById(R.id.txt_task_name);
        dialogSpinner = dialog.findViewById(R.id.project_spinner);

        populateDialogSpinner();
    }

    /**
     * Adds the given task to the list of created tasks.
     *
     * @param task the task to be added to the list
     */
    private void addTask(@NonNull final Task task) {
        tasks.add(task);

        // ajouter une tache dans la BDD
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                taskRepository.insertTask(task);

            }
        });

        updateTasks();
    }

    /**
     * Updates the list of tasks in the UI
     */
    private void updateTasks() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (tasks.size() == 0) {
                    lblNoTasks.setVisibility(View.VISIBLE);
                    listTasks.setVisibility(View.GONE);
                } else {
                    lblNoTasks.setVisibility(View.GONE);
                    listTasks.setVisibility(View.VISIBLE);
                    switch (sortMethod) {
                        case ALPHABETICAL:
                            Collections.sort(tasks, new Task.TaskAZComparator());
                            break;
                        case ALPHABETICAL_INVERTED:
                            Collections.sort(tasks, new Task.TaskZAComparator());
                            break;
                        case RECENT_FIRST:
                            Collections.sort(tasks, new Task.TaskRecentComparator());
                            break;
                        case OLD_FIRST:
                            Collections.sort(tasks, new Task.TaskOldComparator());
                            break;

                    }
                    adapter.updateTasks(tasks);
                }
            }
        });
    }

    /**
     * Returns the dialog allowing the user to create a new task.
     *
     * @return the dialog allowing the user to create a new task
     */
    @NonNull
    private AlertDialog getAddTaskDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);

        alertBuilder.setTitle(R.string.add_task);
        alertBuilder.setView(R.layout.dialog_add_task);
        alertBuilder.setPositiveButton(R.string.add, null);
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogEditText = null;
                dialogSpinner = null;
                dialog = null;
            }
        });

        dialog = alertBuilder.create();

        // This instead of listener to positive button in order to avoid automatic dismiss
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        onPositiveButtonClick(dialog);
                    }
                });
            }
        });

        return dialog;
    }

    /**
     * Sets the data of the Spinner with projects to associate to a new task
     */
    private void populateDialogSpinner() {
        final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allProjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (dialogSpinner != null) {
            dialogSpinner.setAdapter(adapter);
        }
    }

    /**
     * List of all possible sort methods for task
     */
    private enum SortMethod {
        /**
         * Sort alphabetical by name
         */
        ALPHABETICAL,
        /**
         * Inverted sort alphabetical by name
         */
        ALPHABETICAL_INVERTED,
        /**
         * Lastly created first
         */
        RECENT_FIRST,
        /**
         * First created first
         */
        OLD_FIRST,
        /**
         * No sort
         */
        NONE
    }
}
