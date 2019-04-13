package com.bignerdranch.android.todoapp;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.todoapp.database.ToDoDbSchema;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ToDoListFragment extends Fragment {

    private static final String ARG_HEADLINE_ID = "headline_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String GROUP_BY_DATE = ToDoDbSchema.ToDoTable.Cols.DATE;

    private Headline mHeadline;
    private TextView mIfListIsEmptyTextView;
    private RecyclerView mToDoRecyclerView;
    private ToDoAdapter mAdapter;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd.MM ");
    private FloatingActionButton mFloatingActionButton;

    private boolean mBooleanUpdateUiIfCreateNewOrDeleteToDo = false;
    private boolean mSubtitleVisible;
    private boolean mGroupByDate;
    private String mGroupByDateString;
    private boolean mDeleteAllMenuItemVisible;


    public static ToDoListFragment newInstance(UUID headlineId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_HEADLINE_ID, headlineId);

        ToDoListFragment fragment = new ToDoListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID headlineId = (UUID) getArguments().getSerializable(ARG_HEADLINE_ID);
        mHeadline = HeadlineLab.get(getActivity()).getHeadline(headlineId);
        setHasOptionsMenu(true);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_to_do_list, container, false);
        mToDoRecyclerView = v.findViewById(R.id.to_do_recycler_view);
        mToDoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mIfListIsEmptyTextView = v.findViewById(R.id.if_to_do_list_is_empty_text_view);
        mIfListIsEmptyTextView.setText(String.format("%s %s", getString(R.string.if_to_do_list_is_empty_text_view_text), mHeadline.getTitle()));
        mFloatingActionButton = v.findViewById(R.id.floating_action_button_to_do);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewToDo();
            }
        });

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();

    }

    @Override
    public void onPause() {
        super.onPause();
        HeadlineLab.get(getActivity()).updateHeadline(mHeadline);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_to_do_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.ic_menu_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.ic_menu_hide_subtitle);

        } else {
            subtitleItem.setTitle(R.string.ic_menu_show_subtitle);
        }

        MenuItem deleteAllDone = menu.findItem(R.id.ic_menu_delete_all_done);
        if (!mDeleteAllMenuItemVisible) {
            deleteAllDone.setEnabled(false);
        } else {
            deleteAllDone.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_menu_new_to_do:
                addNewToDo();
                return true;
            case R.id.ic_menu_sort_by_date:
                mGroupByDate = !mGroupByDate;
                if (mGroupByDate) {
                    mGroupByDateString = GROUP_BY_DATE;
                } else {
                    mGroupByDateString = null;
                }
                updateUI();
                return true;
            case R.id.ic_menu_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                updateSubtitle();
                return true;
            case R.id.ic_menu_delete_all_done:
                getActivity().invalidateOptionsMenu();
                ToDoLab.get(getActivity()).deleteAllDoneToDo(mHeadline);
                updateUI();
                return true;
            case R.id.ic_menu_delete_all:
                ToDoLab.get(getActivity()).deleteAllToDoWithSuchParent(mHeadline);
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void updateUI() {
        ToDoLab toDoLab = ToDoLab.get(getActivity());
        List<ToDo> toDoes = toDoLab.getToDoesWithSuchParentId(mHeadline.getId(), mGroupByDateString);
        mHeadline.setCounter(toDoes.size());

        if (toDoes.size() == 0) {
            mIfListIsEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mIfListIsEmptyTextView.setVisibility(View.INVISIBLE);
        }

        if (mAdapter == null || mBooleanUpdateUiIfCreateNewOrDeleteToDo) {
            mAdapter = new ToDoAdapter(toDoes);
            mToDoRecyclerView.setAdapter(mAdapter);
            mBooleanUpdateUiIfCreateNewOrDeleteToDo = false;

        } else {
            mAdapter.setToDoes(toDoes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
        updateDeleteMenuItemVisible();

    }

    private void updateDeleteMenuItemVisible() {
        if (ToDoLab.get(getActivity()).getDoneToDoesWithSuchParentId(mHeadline.getId()).size() == 0) {
            mDeleteAllMenuItemVisible = false;
        } else {
            mDeleteAllMenuItemVisible = true;
        }

        getActivity().invalidateOptionsMenu();
    }

    private class ToDoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private ToDo mToDo;
        private TextView mTitleTextView;
        private EditText mTitleEditText;
        private TextView mParentNameTextView;
        private TextView mDateTextView;
        private CheckBox mIsDoneCheckBox;
        private ImageView mDeleteImageView;
        private ImageView mOnDatePlaceDeleteImageView;



        public ToDoHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_to_do, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.to_do_list_item_title_text_view);
            mTitleTextView.setOnClickListener(this);

            mTitleEditText = itemView.findViewById(R.id.to_do_list_item_title_edit_text);
            mTitleEditText.setVisibility(View.INVISIBLE);

            mParentNameTextView = itemView.findViewById(R.id.to_do_list_item_parent_text_view);

            mDateTextView = itemView.findViewById(R.id.to_do_list_item_date_text_view);
            mDateTextView.setOnClickListener(this);

            mIsDoneCheckBox = itemView.findViewById(R.id.to_do_list_item_is_done_check_box);
            mIsDoneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mToDo.setDone(b);
                    ToDoLab.get(getActivity()).updateToDo(mToDo);
                    updateDeleteMenuItemVisible();
                    if (b) {
                        mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mDateTextView.setVisibility(View.INVISIBLE);
                        mOnDatePlaceDeleteImageView.setVisibility(View.VISIBLE);
                    } else {
                        mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        mDateTextView.setVisibility(View.VISIBLE);
                        mOnDatePlaceDeleteImageView.setVisibility(View.INVISIBLE);
                    }
                }
            });

            mDeleteImageView = itemView.findViewById(R.id.to_do_list_item_delete_image_view);
            mDeleteImageView.setOnClickListener(this);
            mDeleteImageView.setVisibility(View.INVISIBLE);
            mOnDatePlaceDeleteImageView = itemView.findViewById(R.id.to_do_list_item_on_date_place_delete_image_view);
            mOnDatePlaceDeleteImageView.setOnClickListener(this);
            mOnDatePlaceDeleteImageView.setVisibility(View.INVISIBLE);

        }

        public void bind(ToDo toDo) {
            mToDo = toDo;
            mTitleTextView.setText(toDo.getTitle());
            mParentNameTextView.setText(mHeadline.getTitle());
            mDateTextView.setText(mDateFormat.format(mToDo.getDate()));
            mIsDoneCheckBox.setChecked(mToDo.isDone());

            DateCompare.compare(mToDo.getDate(), mTitleTextView);
        }


        @Override
        public void onClick(View view) {


            if (view.equals(mTitleTextView)) {
                setEditTextVisibleAndAddTextChangeListener(mTitleTextView, mTitleEditText, mToDo, mParentNameTextView, mDateTextView, mIsDoneCheckBox, mDeleteImageView);
            } else if (view.equals(mDateTextView)) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mToDo.getDate(), mToDo.getId());
                dialog.setTargetFragment(ToDoListFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
            } else if (view.equals(mDeleteImageView) || view.equals(mOnDatePlaceDeleteImageView)) {
                ToDoLab.get(getActivity()).deleteToDo(mToDo);
                mBooleanUpdateUiIfCreateNewOrDeleteToDo = true;
                updateUI();
            } else {
                setEditTextInvisible(mTitleTextView, mTitleEditText, mToDo, mParentNameTextView, mDateTextView, mIsDoneCheckBox, mDeleteImageView);
            }
        }

    }

    private class ToDoAdapter extends RecyclerView.Adapter<ToDoHolder> {

        private List<ToDo> mToDoes;

        public ToDoAdapter(List<ToDo> toDoes) {
            mToDoes = toDoes;
        }

        @NonNull
        @Override
        public ToDoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ToDoHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull ToDoHolder toDoHolder, int i) {
            ToDo toDo = mToDoes.get(i);
            toDoHolder.bind(toDo);
        }

        @Override
        public int getItemCount() {
            return mToDoes.size();
        }

        public void setToDoes(List<ToDo> toDoes) {
            mToDoes = toDoes;
        }
    }

    private void setEditTextVisibleAndAddTextChangeListener(TextView textView, EditText editText, final ToDo toDo, TextView parentTextView, TextView dateTextView, CheckBox checkBox, ImageView imageView) {
        textView.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.VISIBLE);
        if (textView.getText().equals(getString(R.string.new_to_do_default_title))) {
            editText.setText("");
        } else {
            editText.setText(textView.getText());
        }
        parentTextView.setVisibility(View.INVISIBLE);
        checkBox.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
        dateTextView.setVisibility(View.INVISIBLE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                toDo.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        ToDoLab.get(getActivity()).updateToDo(toDo);
    }

    private void setEditTextInvisible(TextView textView, EditText editText, ToDo toDo, TextView parentTextView, TextView dateTextView, CheckBox checkBox, ImageView imageView) {
        editText.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
        textView.setText(toDo.getTitle());
        parentTextView.setVisibility(View.VISIBLE);
        dateTextView.setVisibility(View.VISIBLE);
        checkBox.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);

        ToDoLab.get(getActivity()).updateToDo(toDo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            UUID todoId = (UUID) data.getSerializableExtra(DatePickerFragment.EXTRA_UUID);

            ToDoLab toDoLab = ToDoLab.get(getActivity());
            List<ToDo> toDoes = toDoLab.getToDoesWithSuchParentId(mHeadline.getId(), null);

            for (ToDo toDo : toDoes) {
                if (toDo.getId().equals(todoId)) {
                    toDo.setDate(date);
                    ToDoLab.get(getActivity()).updateToDo(toDo);
                    Log.i("ToDoListFragment", toDo.getTitle() + date + " date seted");
                    Log.i("ToDoListFragment", toDo.getDate() + "  current date");
                }


            }

            updateUI();


        }
    }

    private void updateSubtitle() {
        int toDoCount = ToDoLab.get(getActivity()).getToDoesWithSuchParentId(mHeadline.getId(), null).size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plurals, toDoCount, toDoCount);
        String title = mHeadline.getTitle();
        if (!mSubtitleVisible) {
            subtitle = null;
            title = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
        activity.getSupportActionBar().setTitle(title);


    }

    private void addNewToDo() {
        ToDo toDo = new ToDo(mHeadline, getString(R.string.new_to_do_default_title));
        ToDoLab.get(getActivity()).addToDo(toDo);
        mBooleanUpdateUiIfCreateNewOrDeleteToDo = true;
        updateUI();
    }


}
