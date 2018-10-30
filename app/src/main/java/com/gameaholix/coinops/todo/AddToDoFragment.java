package com.gameaholix.coinops.todo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.FragmentAddToDoBinding;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// TODO: finish this

public class AddToDoFragment extends Fragment {
    private static final String TAG = AddToDoFragment.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";

    private Context mContext;
    private String mGameId;
    private ToDoItem mNewToDoItem;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    public AddToDoFragment() {
        // Required empty public constructor
    }

    public static AddToDoFragment newInstance(String gameId) {
        Bundle args = new Bundle();
        AddToDoFragment fragment = new AddToDoFragment();
        args.putString(EXTRA_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mGameId = getArguments().getString(EXTRA_GAME_ID);
            }
            mNewToDoItem = new ToDoItem();
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mNewToDoItem = savedInstanceState.getParcelable(EXTRA_TODO);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this
        final FragmentAddToDoBinding bind = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_to_do, container, false);
        final View rootView = bind.getRoot();

        //Setup EditTexts
        bind.etTodoName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(textView);
                }
                return false;
            }
        });
        bind.etTodoName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_game_name && !hasFocus) {
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        hideKeyboard(editText);
                    }
                }
            }
        });

        bind.etTodoDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(textView);
                }
                return false;
            }
        });
        bind.etTodoDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.et_game_name && !hasFocus) {
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        hideKeyboard(editText);
                    }
                }
            }
        });

        // Setup Spinner
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                mContext, R.array.priority, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bind.spinnerPriority.setAdapter(priorityAdapter);
        bind.spinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mNewToDoItem.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


//        // Setup Switch
//        bind.switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
//                mNewToDoItem.setReminder(isOn);
//                if (isOn) {
//                    bind.tvReminderDate.setVisibility(View.VISIBLE);
//                    bind.ibReminderDateArrow.setVisibility(View.VISIBLE);
//                    bind.tvReminderTime.setVisibility(View.VISIBLE);
//                    bind.ibReminderTimeArrow.setVisibility(View.VISIBLE);
//                } else {
//                    bind.tvReminderDate.setVisibility(View.GONE);
//                    bind.ibReminderDateArrow.setVisibility(View.GONE);
//                    bind.tvReminderTime.setVisibility(View.GONE);
//                    bind.ibReminderTimeArrow.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        // Setup date picker dialog
//        // Get Current date
//        final Calendar calendar = Calendar.getInstance();
//        int currentYear = calendar.get(Calendar.YEAR);
//        int currentMonth = calendar.get(Calendar.MONTH);
//        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
//
//        final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                String datePicked = (month + 1)  + "-" + day + "-" + year;
//                bind.tvReminderDate.setText(datePicked);
//            }
//        }, currentYear, currentMonth, currentDay);
//
//        // Setup date onClickListener
//        View.OnClickListener dateListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                datePickerDialog.show();
//            }
//        };
//        bind.tvReminderDate.setOnClickListener(dateListener);
//        bind.ibReminderDateArrow.setOnClickListener(dateListener);
//
//        // Setup time picker dialog
//        // Get current time
//        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
//        int currentMinute = calendar.get(Calendar.MINUTE);
//
//        final TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
//                String timePicked = hour + ":" + minute;
//                bind.tvReminderTime.setText(timePicked);
//            }
//        }, currentHour, currentMinute, false);
//
//        // Setup time onClickListener
//        View.OnClickListener timeListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                timePickerDialog.show();
//            }
//        };
//        bind.tvReminderTime.setOnClickListener(timeListener);
//        bind.ibReminderTimeArrow.setOnClickListener(timeListener);

        // Setup Button
        Button addButton = bind.btnSave;
        addButton.setText(R.string.add_item);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewToDoItem.setName(bind.etTodoName.getText().toString().trim());
                mNewToDoItem.setDescription(bind.etTodoDescription.getText().toString().trim());
                addItem(mNewToDoItem);
                getActivity().finish();
            }
        });

        return rootView;
    }

    private void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putParcelable(EXTRA_TODO, mNewToDoItem);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void addItem(ToDoItem item) {
        if (TextUtils.isEmpty(item.getName())) {
            PromptUser.displayAlert(mContext,
                    R.string.error_add_item_failed,
                    R.string.error_item_name_empty);
            return;
        }

        // TODO: add checks for if item name already exists.

        // Add Entry object to Firebase
        if (mUser != null) {
            // user is signed in

            String uid = mUser.getUid();
            String id = mDatabaseReference.child(Db.TODO).child(uid).push().getKey();

            // Get database paths from helper class
            String toDoPath = Db.getToDoPath(uid) + id;
            String gameToDoListPath = Db.getGameToDoListPath(uid, mGameId) + id;
            String userToDoListPath = Db.getUserToDoListPath(uid) + id;

            Map<String, Object> valuesToAdd = new HashMap<>();
            valuesToAdd.put(toDoPath, item);
            valuesToAdd.put(gameToDoListPath, item.getName());
            valuesToAdd.put(userToDoListPath, item.getName());

            Log.d(TAG, valuesToAdd.toString());

            mDatabaseReference.updateChildren(valuesToAdd, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError,
                                       @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                " Code: " + databaseError.getCode() +
                                " Details: " + databaseError.getDetails());
                    }
                }
            });

//        } else {
//            // user is not signed in
        }
    }

}
