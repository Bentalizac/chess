package server;

import webSocketMessages.notifications.Notification;

public class NotificationHandler {
    public void notify(Notification notification) {
        System.out.println(notification.message());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n"  + ">>> ");
    }
}
