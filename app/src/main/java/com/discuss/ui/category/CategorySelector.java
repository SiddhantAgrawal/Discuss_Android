package com.discuss.ui.category;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.discuss.DiscussApplication;
import com.discuss.datatypes.PersonCategoryPreference;
import com.discuss.ui.View;
import com.example.siddhantagrawal.check_discuss.R;

import javax.inject.Inject;

import rx.functions.Action0;


public class CategorySelector extends AppCompatActivity implements View {

    @Inject
    public CategorySelectorPresenter categorySelectorPresenter;
    CategorySelectorAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void init(Action0 onCompletedAction) {
        categorySelectorPresenter.init(onCompletedAction);
    }


    @SuppressWarnings(value = "unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DiscussApplication) getApplication()).getMainComponent().inject(this);
        setTitle("Categories");
        setContentView(R.layout.tag_list);
        categorySelectorPresenter.init(new Action0() {
            @Override
            public void call() {
                ListView listView = findViewById(R.id.tag_list);
                adapter = new CategorySelector.CategorySelectorAdapter(CategorySelector.this, categorySelectorPresenter);
                listView.setAdapter(adapter);

                Button button = findViewById(R.id.tag_list_button);
                button.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        //@Todo(Deepak) : Implement this to actucally make a real save
                        Toast.makeText(CategorySelector.this, "your choices has been added successfully",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public static class CategorySelectorAdapter extends BaseAdapter {

        // Declare Variables
        private Context context;
        private CategorySelectorPresenter categorySelectorPresenter;

        public CategorySelectorAdapter(Context context, CategorySelectorPresenter categorySelectorPresenter) {
            this.context = context;
            this.categorySelectorPresenter = categorySelectorPresenter;
        }

        @Override
        public int getCount() {
            return categorySelectorPresenter.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public android.view.View getView(final int position, android.view.View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            android.view.View tagEntity = inflater.inflate(R.layout.tag_entity, parent, false);
            final PersonCategoryPreference personCategoryPreferences = categorySelectorPresenter.get(position).toBlocking().first();
            TextView questionText = (TextView) tagEntity.findViewById(R.id.tag_entity_category);
            CheckBox checkBox = (CheckBox) tagEntity.findViewById(R.id.tag_entity_pref);
            questionText.setText(personCategoryPreferences.getCategory().name());
            checkBox.setChecked(personCategoryPreferences.getPreferred());

            return tagEntity;
        }
    }

}
