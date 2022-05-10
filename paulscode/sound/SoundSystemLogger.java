package paulscode.sound;

public class SoundSystemLogger {
    public void message(String message, int indent) {
        String spacer = "";
        for (int x2 = 0; x2 < indent; ++x2) {
            spacer = spacer + "    ";
        }
        String messageText = spacer + message;
        System.out.println(messageText);
    }

    public void importantMessage(String message, int indent) {
        String spacer = "";
        for (int x2 = 0; x2 < indent; ++x2) {
            spacer = spacer + "    ";
        }
        String messageText = spacer + message;
        System.out.println(messageText);
    }

    public boolean errorCheck(boolean error, String classname, String message, int indent) {
        if (error) {
            this.errorMessage(classname, message, indent);
        }
        return error;
    }

    public void errorMessage(String classname, String message, int indent) {
        String spacer = "";
        for (int x2 = 0; x2 < indent; ++x2) {
            spacer = spacer + "    ";
        }
        String headerLine = spacer + "Error in class '" + classname + "'";
        String messageText = "    " + spacer + message;
        System.out.println(headerLine);
        System.out.println(messageText);
    }

    public void printStackTrace(Exception e2, int indent) {
        this.printExceptionMessage(e2, indent);
        this.importantMessage("STACK TRACE:", indent);
        if (e2 == null) {
            return;
        }
        StackTraceElement[] stack = e2.getStackTrace();
        if (stack == null) {
            return;
        }
        for (int x2 = 0; x2 < stack.length; ++x2) {
            StackTraceElement line = stack[x2];
            if (line == null) continue;
            this.message(line.toString(), indent + 1);
        }
    }

    public void printExceptionMessage(Exception e2, int indent) {
        this.importantMessage("ERROR MESSAGE:", indent);
        if (e2.getMessage() == null) {
            this.message("(none)", indent + 1);
        } else {
            this.message(e2.getMessage(), indent + 1);
        }
    }
}

