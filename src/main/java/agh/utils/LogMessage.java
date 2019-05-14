package agh.utils;

import java.io.Serializable;

public class LogMessage implements Serializable {
    LogLevel level;
    String agent;
    String time;
    String message;

    public LogMessage(LogLevel level, String agent, String time, String message) {
        this.level = level;
        this.agent = agent;
        this.time = time;
        this.message = message;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getAgent() {
        return agent;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getLog(){
        return time + " " + agent + ": [" + level.toString()  + "] " + message;
    }
}