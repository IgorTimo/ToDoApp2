package com.bignerdranch.android.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HeadlineListFragment extends Fragment {

    private static final int REQUEST_DELETE_ALL = 0;

    private RecyclerView mHeadlineRecyclerView;
    private HeadlineAdapter mAdapter;
    private boolean mBooleanUpdateUiIfCreateNewOrDeleteHeadline = false;
    private TextView mIfListIsEmptyTextView;
    private FloatingActionButton mFloatingActionButton;
    private boolean mIsTestSingletonAdded;

    public static HeadlineListFragment newInstance() {
        return new HeadlineListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mIsTestSingletonAdded = Preferences.isSingletonAdded(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_headline_list, container, false);
        mHeadlineRecyclerView = v.findViewById(R.id.headline_recycler_view);
        mHeadlineRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mIfListIsEmptyTextView = v.findViewById(R.id.if_headline_list_is_empty_text_view);
        mFloatingActionButton = v.findViewById(R.id.floating_action_button_headline);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewHeadline();
            }
        });


        updateUI();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_headline_list, menu);

        MenuItem singletonItem = menu.findItem(R.id.maim_menu_set_example_lists);
        if (!mIsTestSingletonAdded) {
            singletonItem.setTitle(R.string.maim_menu_set_example_lists);
        } else {
            singletonItem.setTitle(R.string.maim_menu_delete_example_lists);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_new_category:
                addNewHeadline();
                return true;
            case R.id.maim_menu_set_example_lists:
                updateSingletonAddedMenuItem();
                return true;
            case R.id.main_menu_delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.delete_all_tasks_alert_dialog_title);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAll();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }


    }


    private void updateUI() {
        HeadlineLab headlineLab = HeadlineLab.get(getActivity());
        List<Headline> headlines = headlineLab.getHeadlines();
        if (headlines.size() == 0) {
            mIfListIsEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mIfListIsEmptyTextView.setVisibility(View.INVISIBLE);
        }

        if (mAdapter == null || mBooleanUpdateUiIfCreateNewOrDeleteHeadline) {
            mAdapter = new HeadlineAdapter(headlines);
            mHeadlineRecyclerView.setAdapter(mAdapter);
            mBooleanUpdateUiIfCreateNewOrDeleteHeadline = false;
        } else {
            mAdapter.setHeadlines(headlines);
            mAdapter.notifyDataSetChanged();
        }
    }


    private class HeadlineHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private Headline mHeadline;

        private TextView mTitleTextView;
        private EditText mTitleEditText;
        private TextView mCounterTextView;
        private ImageView mDeleteImageView;

        public HeadlineHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_headline, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.headline_list_item_title_text_view);
            mTitleTextView.setOnClickListener(this);
            mTitleTextView.setOnLongClickListener(this);

            mTitleEditText = itemView.findViewById(R.id.headline_list_item_title_edit_text);
            mTitleEditText.setVisibility(View.INVISIBLE);

            mCounterTextView = itemView.findViewById(R.id.headline_list_item_counter_text_view);

            mDeleteImageView = itemView.findViewById(R.id.headline_list_item_delete_image_view);
            mDeleteImageView.setOnClickListener(this);
            mDeleteImageView.setVisibility(View.INVISIBLE);
        }

        public void bind(Headline headline) {
            mHeadline = headline;
            mTitleTextView.setText(headline.getTitle());
            mCounterTextView.setText(String.valueOf(headline.getCounter()));
        }

        @Override
        public void onClick(View view) {
            if (mTitleEditText.getVisibility() == View.VISIBLE) {
                if (view.equals(mDeleteImageView)) {
                    HeadlineLab.get(getActivity()).deleteHeadline(mHeadline);
                    mBooleanUpdateUiIfCreateNewOrDeleteHeadline = true;
                    updateUI();
                } else {
                    setEditTextInvisible(mTitleEditText, mTitleTextView, mHeadline, mCounterTextView, mDeleteImageView);
                }
            } else {
                Intent intent = ToDoesPagerActivity.newIntent(getActivity(), mHeadline.getId());
                startActivity(intent);
            }
        }


        @Override
        public boolean onLongClick(View view) {
            setEditTextVisibleAndAddTextChangeListener(mTitleEditText, mTitleTextView, mHeadline, mCounterTextView, mDeleteImageView);
            return true;
        }
    }

    private class HeadlineAdapter extends RecyclerView.Adapter<HeadlineHolder> {

        private List<Headline> mHeadlines;

        public HeadlineAdapter(List<Headline> headlines) {
            mHeadlines = headlines;
        }

        @NonNull
        @Override

        public HeadlineHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new HeadlineHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull HeadlineHolder headlineHolder, int i) {
            Headline headline = mHeadlines.get(i);
            headlineHolder.bind(headline);
        }

        @Override
        public int getItemCount() {
            return mHeadlines.size();
        }

        public void setHeadlines(List<Headline> headlines) {
            mHeadlines = headlines;
        }
    }

    private void addNewHeadline() {
        Headline headline = new Headline(getString(R.string.new_headline_default_title), 0);
        HeadlineLab.get(getActivity()).addHeadline(headline);
        mBooleanUpdateUiIfCreateNewOrDeleteHeadline = true;
        updateUI();
    }

    private void changeSingletonAddedOption() {
        mIsTestSingletonAdded = !mIsTestSingletonAdded;
        Preferences.setSingletonAdded(getActivity(), mIsTestSingletonAdded);
        getActivity().invalidateOptionsMenu();
    }

    private void updateSingletonAddedMenuItem() {
        changeSingletonAddedOption();
        if (mIsTestSingletonAdded) {
            HeadlineLab.get(getActivity()).setTestSingletonHeadlines();
            ToDoLab.get(getActivity()).setTestSingletonToDoes();
        } else {
            HeadlineLab.get(getActivity()).deleteTestSingletonHeadlines();
        }
        updateUI();
    }

    private void deleteAll() {
        mIsTestSingletonAdded = false;
        Preferences.setSingletonAdded(getActivity(), mIsTestSingletonAdded);
        getActivity().invalidateOptionsMenu();
        HeadlineLab.get(getActivity()).deleteAll();
        updateUI();
    }

    public static void deleteAllAlertDialogChose(boolean delete) {
        if (delete) {

        }
    }

    private void setEditTextVisibleAndAddTextChangeListener(EditText editText, TextView textView, final Headline headline, TextView counterTextView, ImageView imageView) {
        textView.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.VISIBLE);
        if (textView.getText().equals(getString(R.string.new_headline_default_title))) {
            editText.setText("");
        } else {
            editText.setText(textView.getText());
        }
        counterTextView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                headline.setTitle(charSequence.toString());
                HeadlineLab.get(getActivity()).updateHeadline(headline);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setEditTextInvisible(EditText editText, TextView textView, Headline headline, TextView counterTextView, ImageView imageView) {
        editText.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
        textView.setText(headline.getTitle());
        counterTextView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
    }


}
