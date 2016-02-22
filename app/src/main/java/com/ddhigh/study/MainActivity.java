package com.ddhigh.study;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewItemFragment.OnNewItemAddedListener {
    ArrayList<ToDoItem> items = new ArrayList<>();
    ToDoItemAdapter aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //UI组件
        //数据适配器
        aa = new ToDoItemAdapter(this, R.layout.todolist_item, items);
        //获取fragment引用
        FragmentManager fragmentManager = getFragmentManager();
        ToDoListFragment toDoListFragment = (ToDoListFragment) fragmentManager.findFragmentById(R.id.TodoListFragment);
        toDoListFragment.setListAdapter(aa);
    }

    @Override
    public void onNewItemAdded(String newItem) {
        ToDoItem doItem = new ToDoItem(newItem);
        items.add(0, doItem);
        aa.notifyDataSetChanged();
    }
}
