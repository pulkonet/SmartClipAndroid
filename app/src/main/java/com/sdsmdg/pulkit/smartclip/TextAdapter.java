package com.sdsmdg.pulkit.smartclip;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;


public class TextAdapter extends ArrayAdapter<ClippedText> {

    public TextAdapter(@NonNull Context context, @LayoutRes int resource, List<ClippedText> objects) {
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
}
