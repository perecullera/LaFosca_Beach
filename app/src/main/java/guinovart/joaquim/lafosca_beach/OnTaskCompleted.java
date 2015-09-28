package guinovart.joaquim.lafosca_beach;

import guinovart.joaquim.lafosca_beach.Models.Beach;

public interface OnTaskCompleted{
    //method implemented on BeachActivity for openning closing the beach
    void onTaskCompleted(Beach beach);
    //method implemented on MainActivity used for login signin
    void onTaskCompleted(String token);

}
