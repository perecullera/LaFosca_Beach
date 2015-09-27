package guinovart.joaquim.lafosca_beach;

import guinovart.joaquim.lafosca_beach.Models.Beach;

public interface OnTaskCompleted{

    void onTaskCompleted(Beach beach);
    void onTaskCompleted(String token);

}
