package utils;

import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class NumberTextField extends TextField
{
    private static Pattern integerPattern = Pattern.compile("[0-9]*");
    private String timeFieldType = "hour";// default attribute

    public String getTimeFieldType() {
        return timeFieldType;
    }

    public void setTimeFieldType(String timeFieldType) {
        this.timeFieldType = timeFieldType;
    }

    @Override
    public void replaceText(int start, int end, String text)
    {
        // if the character is valid
        if (validate(text))
        {
            String numStr = this.getCharacters().toString();
            if(numStr.length() == 1 && this.getCaretPosition()  == 0){
                numStr = text + numStr;// add to the front for the check
            } else {
                numStr += text;// add to the end
            }

            int numInt = Integer.parseInt(numStr);
            if(timeFieldType.toLowerCase().equals("hour")){
                if(numInt >= 1 && numInt <= 12){
                    super.replaceText(start, end, text);
                }
            } else if (timeFieldType.toLowerCase().equals("min")){
                if(numInt >= 1 && numInt <= 59){
                    super.replaceText(start, end, text);
                }
            }
        }
    }

    @Override
    public void replaceSelection(String text)
    {
        if (validate(text))
        {
            super.replaceSelection(text);
        }
    }

    private boolean validate(String text)
    {
        return integerPattern.matcher(text).matches();
    }
}
