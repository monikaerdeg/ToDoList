package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.RestClientException;

import java.util.List;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.adapter.TaskAdapter;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.constant.ResultConstants;
import eu.execom.todolistgrouptwo.model.Task;
import eu.execom.todolistgrouptwo.model.User;
import eu.execom.todolistgrouptwo.preference.UserPreferences_;

/**
 * Home {@link AppCompatActivity Activity} for navigation and listing all tasks.
 */
@EActivity(R.layout.activity_home)
@OptionsMenu(R.menu.main_menu)
public class HomeActivity extends AppCompatActivity {

    /**
     * Used for logging purposes.
     */
    private static final String TAG = HomeActivity.class.getSimpleName();

    /**
     * Used for identifying results from different activities.
     */
    protected static final int ADD_TASK_REQUEST_CODE = 42;
    protected static final int TASK_DETAIL_REQUEST_CODE = 24;
    protected static final int LOGIN_REQUEST_CODE = 420; // BLAZE IT

    /**
     * Tasks are kept in this list during a user session.
     */
    private List<Task> tasks;

    /**
     * {@link FloatingActionButton FloatingActionButton} for starting the
     * {@link AddTaskActivity AddTaskActivity}.
     */
    @ViewById
    FloatingActionButton addTask;

    //@ViewById
    //Button logout;

    /**
     * {@link ListView ListView} for displaying the tasks.
     */
    @ViewById
    ListView listView;

    //@OptionsMenuItem
   //MenuItem logout;

    /**
     * {@link TaskAdapter Adapter} for providing data to the {@link ListView listView}.
     */
    @Bean
    TaskAdapter adapter;

    @Pref
    UserPreferences_ userPreferences;

    @RestService
    RestApi restApi;

    @AfterViews
    @Background
    void checkUser() {
        if (!userPreferences.accessToken().exists()) {
            LoginActivity_.intent(this).startForResult(LOGIN_REQUEST_CODE);
            return;
        }

        try {
            tasks = restApi.getAllTasks();
        } catch (RestClientException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        initData();
    }

    /**
     * Loads tasks from the {@link android.content.SharedPreferences SharedPreferences}
     * and sets the adapter.
     */
    @UiThread
    void initData() {
        listView.setAdapter(adapter);
        adapter.setTasks(tasks);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    // button show
                    addTask.show();
                } else {
                    // button hide
                    addTask.hide();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * Called when the {@link FloatingActionButton FloatingActionButton} is clicked.
     */
    @Click
    void addTask() {
        Log.i(TAG, "Add task clicked!");
        AddTaskActivity_.intent(this).startForResult(ADD_TASK_REQUEST_CODE);
    }

    @OptionsItem
    void logout(){
        userPreferences.accessToken().remove();
        checkUser();
    }

    @ItemClick
    void listViewItemClicked(Task task){
        Intent intent = new Intent(this, TaskDetailActivity_.class);
        final Gson gson = new Gson();
        intent.putExtra("task",gson.toJson(task));
        startActivityForResult(intent,TASK_DETAIL_REQUEST_CODE);
    }

    /**
     * Called when the {@link AddTaskActivity AddTaskActivity} finishes.
     *
     * @param resultCode Indicates whether the activity was successful.
     * @param task         The new task.
     */
    @OnActivityResult(ADD_TASK_REQUEST_CODE)
    @Background
    void onResult(int resultCode, @OnActivityResult.Extra String task) {
        if (resultCode == RESULT_OK) {
            final Gson gson = new Gson();
            final Task newTask = gson.fromJson(task, Task.class);

            try {
                restApi.createTask(newTask);
                tasks = restApi.getAllTasks();
                onTaskCreated(newTask);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    @OnActivityResult(TASK_DETAIL_REQUEST_CODE)
    @Background
    void onDetailResult(int resultCode, @OnActivityResult.Extra String task) {

        if(resultCode != RESULT_CANCELED) {

            final Gson gson = new Gson();
            final Task editedTask = gson.fromJson(task, Task.class);

            if (resultCode == ResultConstants.RESULT_UPDATE) {
                try {
                    restApi.updateTask(editedTask, (int) editedTask.getId());
                    onTaskUpdated(editedTask);

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            } else if (resultCode == ResultConstants.RESULT_DELETE) {
                try {
                    restApi.deleteTask((int) editedTask.getId());
                    onTaskDeleted(editedTask);
                }
                catch (Exception e){
                    Log.e(TAG, e.getMessage(), e);
                }
            }

        }
    }

    @UiThread
    void onTaskDeleted(Task editedTask) {
        int position = 0;

        for(int i = 0; i<tasks.size(); i++){
            if(tasks.get(i).getId() == editedTask.getId()){
                position = i;
            }
        }
        tasks.remove(position);

        adapter.setTasks(tasks);
    }

    @UiThread
    void onTaskUpdated(Task editedTask){
        int position = 0;

        for(int i = 0; i<tasks.size(); i++){
            if(tasks.get(i).getId() == editedTask.getId()){
                position = i;
            }
        }
        tasks.remove(position);
        tasks.add(position,editedTask);

        adapter.setTasks(tasks);
    }

    @UiThread
    void onTaskCreated(Task task) {
        adapter.setTasks(tasks);
    }

    @OnActivityResult(LOGIN_REQUEST_CODE)
    void onLogin(int resultCode, @OnActivityResult.Extra("token") String token) {
        if (resultCode == RESULT_OK) {
            userPreferences.accessToken().put(token);
            checkUser();
        }else{
            finish();
        }
    }

}
