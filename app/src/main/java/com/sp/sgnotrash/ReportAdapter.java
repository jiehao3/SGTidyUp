package com.sp.sgnotrash;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.sgnotrash.R;
import com.sp.sgnotrash.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<Report> reports;

    private OnReportClickListener listener;
    public interface OnReportClickListener {
        void onReportClick(Report report);
    }
    public ReportAdapter(List<Report> reports, OnReportClickListener listener) {
        this.reports = reports;
        this.listener = listener;
    }


    public ReportAdapter(List<Report> reports) {
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.tvDescription.setText(report.getDescription());
        holder.tvUser.setText(report.getName());
        byte[] decodedString = Base64.decode(report.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.ivReportImage.setImageBitmap(decodedByte);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReportClick(report);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView ivReportImage;
        TextView tvDescription, tvUser;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            ivReportImage = itemView.findViewById(R.id.ivReportImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUser = itemView.findViewById(R.id.tvUser);
        }
    }
}