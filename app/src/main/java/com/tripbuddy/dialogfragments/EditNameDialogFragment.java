package com.tripbuddy.dialogfragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tripbuddy.R;
import com.tripbuddy.SettingsActivity;

import org.jetbrains.annotations.NotNull;

public class EditNameDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {
    int editType; // will be equal to one of the above (NAME, USERNAME, PASSWORD)
    TextView tvTitle;
    EditText etInput;
    EditText etInputConfirm;

    public EditNameDialogFragment() {
        // empty constructor required
    }

    public static EditNameDialogFragment newInstance(int editType, String title, String name) {
        EditNameDialogFragment frag = new EditNameDialogFragment();
        Bundle args = new Bundle();
        args.putInt("editType", editType);
        args.putString("title", title);
        args.putString("name", name);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_name, container);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editType = getArguments().getInt("editType");
        tvTitle = view.findViewById(R.id.tvEdit);
        etInput = view.findViewById(R.id.etEdit);
        etInputConfirm = view.findViewById(R.id.etEditConfirm);

        String title = getArguments().getString("title", "Edit");
        getDialog().setTitle(title);
        tvTitle.setText(title);

        if (editType == SettingsActivity.PASSWORD) {
            etInput.setHint("New Password");
            etInputConfirm.setHint("Confirm New Password");
            etInputConfirm.setVisibility(View.VISIBLE);
            etInputConfirm.setOnEditorActionListener(this);
        } else {
            etInput.setInputType(InputType.TYPE_CLASS_TEXT);
            etInput.setText(getArguments().getString("name", ""));
            etInput.setOnEditorActionListener(this);
        }

        etInput.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            String input = etInput.getText().toString();

            if (editType == SettingsActivity.PASSWORD) {
                String passwordConfirm = etInputConfirm.getText().toString();
                if (!input.equals(passwordConfirm)) {
                    Toast.makeText(getActivity(), "Passwords must match", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            EditNameDialogListener listener = (EditNameDialogListener) getActivity();
            listener.onFinishEditDialog(editType, input);
            dismiss();
            return true;
        }
        return false;
    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(int editType, String inputText);
    }
}
