package interfaces;

import model.User;

public interface CommonAction {
    void loginAs(User validUser);
     void launchApplication();
     void validateHomePage();
}
