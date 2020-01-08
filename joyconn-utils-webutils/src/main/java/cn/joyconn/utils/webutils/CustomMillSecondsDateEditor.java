package cn.joyconn.utils.webutils;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CustomMillSecondsDateEditor extends PropertyEditorSupport {

    /**
     * @see PropertyEditorSupport#setAsText(String)
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if(text.equals("")){
            setValue(new Date());
        }else{
            setValue(new Date(Long.decode(text)));
        }
    }

    /**
     * @see PropertyEditorSupport#getAsText()
     */
    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        return (value != null ? String.valueOf(TimeUnit.MILLISECONDS.toSeconds(value.getTime())) : "");
    }

}