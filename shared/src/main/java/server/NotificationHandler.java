package server;


import webSocketMessages.serverMessages.Notification;

public class NotificationHandler {
    public void notify(Notification notification) {
        System.out.println(notification.getMessage());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n"  + ">>> ");
    }
}
