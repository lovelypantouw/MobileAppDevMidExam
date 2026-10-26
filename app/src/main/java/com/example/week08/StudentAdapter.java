package com.example.week08;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    List<StudentModel> student;
    Context context;
    DBHelper dbHelper;

    public StudentAdapter(List<StudentModel> student, Context context) {
        this.student = student;
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.student_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final StudentModel studentModel = student.get(position);

        holder.textViewID.setText("Record No: " + studentModel.getId());
        holder.editTextName.setText(studentModel.getName());
        holder.editTextStudentId.setText(studentModel.getStudentId());
        holder.checkEnrolled.setChecked(studentModel.isEnrolled());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strName = holder.editTextName.getText().toString().trim();
                String strStudentId = holder.editTextStudentId.getText().toString().trim();
                boolean isEnrolled = holder.checkEnrolled.isChecked();

                if (strName.length() <= 0 || strStudentId.length() <= 0) {
                    Toast.makeText(context, "Enter data", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Student ID must stay unique (ignore this row itself)
                if (dbHelper.isStudentIdExists(strStudentId, studentModel.getId())) {
                    holder.editTextStudentId.setError("Student ID already exists");
                    Toast.makeText(context, "Student ID already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = dbHelper.updateStudent(
                        new StudentModel(studentModel.getId(), strName, strStudentId, isEnrolled));

                if (success) {
                    Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    ((Activity) context).finish();
                    context.startActivity(((Activity) context).getIntent());
                } else {
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteStudent(studentModel.getId());
                student.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return student.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewID;
        EditText editTextName, editTextStudentId;
        CheckBox checkEnrolled;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewID = itemView.findViewById(R.id.textViewID);
            editTextName = itemView.findViewById(R.id.editTextName);
            editTextStudentId = itemView.findViewById(R.id.editTextStudentId);
            checkEnrolled = itemView.findViewById(R.id.checkEnrolled);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
