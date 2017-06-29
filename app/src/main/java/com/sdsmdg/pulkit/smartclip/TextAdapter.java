package com.sdsmdg.pulkit.smartclip;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import static java.security.AccessController.getContext;


public class TextAdapter extends RecyclerView.Adapter<TextAdapter.ViewHolder> {

   /* public TextAdapter(@NonNull Context context, @LayoutRes int resource, List<ClippedText> objects) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.clipped_text_view, parent, false);
        }

        TextView timeTextView = (TextView) convertView.findViewById(R.id.time);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.name);
        final EditText clippedText = (EditText) convertView.findViewById(R.id.clippedText);
        ImageView copyClippedText =(ImageView)convertView.findViewById(R.id.copyClippedText);
        ClippedText clippedText1 = getItem(position);

        authorTextView.setText(clippedText1.getEmail());
        timeTextView.setText(clippedText1.getTime());
        clippedText.setText(clippedText1.getText());
        copyClippedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", clippedText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),"Copied to Clipboard",Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
*/
    @Override
    public TextAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View clippedTextView =inflater.inflate(R.layout.clipped_text_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(clippedTextView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TextAdapter.ViewHolder holder, int position) {
        ClippedText clippedText=mClippedText.get(position);
        TextView authorTextView = holder.authorTextView;
        authorTextView.setText(clippedText.getEmail());
        TextView timeTextView=holder.timeTextView;
        final EditText clippedEditText=holder.clippedText;
        ImageView copyClippedText=holder.copyClippedText;
        timeTextView.setText(clippedText.getTime());
        clippedEditText.setText(clippedText.getText());
        copyClippedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", clippedEditText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),"Copied to Clipboard",Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return mClippedText.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView timeTextView;
        TextView authorTextView;
        EditText clippedText;
        ImageView copyClippedText;

        public ViewHolder(View convertView) {
            super(convertView);

            timeTextView = (TextView) convertView.findViewById(R.id.time);
            authorTextView = (TextView) convertView.findViewById(R.id.name);
            clippedText = (EditText) convertView.findViewById(R.id.clippedText);
            copyClippedText =(ImageView)convertView.findViewById(R.id.copyClippedText);
        }
    }

    private List<ClippedText> mClippedText;
    private Context mContext;

    public TextAdapter(Context context, List<ClippedText> clippedTexts){
        mClippedText=clippedTexts;
        mContext=context;
    }

    private Context getContext() {
        return mContext;
    }
}
