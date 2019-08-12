package com.example.sharecontacts_contentresolver_9_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class personAdapter extends ArrayAdapter <Person>
{
    private  int viewId;
    public personAdapter(Context context, int viewResId, List<Person> person_list)
    {
        super(context,viewResId,person_list);
        viewId=viewResId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View itemLayout;
        TextViewHolder holder;
        Person person=getItem(position);
        if(convertView==null)
        {
            itemLayout=LayoutInflater.from(getContext()).
                    inflate(viewId,parent,false);
            holder=new TextViewHolder();
            holder.tv_name=(TextView)itemLayout.findViewById(R.id.name);
            holder.tv_phone=(TextView)itemLayout.findViewById(R.id.phone);
            itemLayout.setTag(holder);
        }
        else
        {
            itemLayout=convertView;
            holder=(TextViewHolder) itemLayout.getTag();
        }

        holder.tv_name.setText(person.getPersonId()+"  "+person.getName());
        holder.tv_phone.setText(person.getPhone());
        return  itemLayout;
    }

    class TextViewHolder
    {
        TextView tv_name;
        TextView tv_phone;
    }

}
