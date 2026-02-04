package de.icod.techidon.model;

import de.icod.techidon.api.RequiredField;
import org.parceler.Parcel;

import java.util.List;

@Parcel
public class CustomLocalTimeline extends BaseModel{
    @RequiredField
    public String domain;

    @Override
    public String toString(){
        return "Hashtag{"+
                ", url='"+domain+'\''+
                '}';
    }
}
