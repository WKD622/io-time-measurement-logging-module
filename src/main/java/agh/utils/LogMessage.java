package agh.utils;

import java.io.Serializable;

public class LogMessage implements Serializable {
    LogLevel level;
    Agents agent;
    String time;
    LogMessageType type;
    String message;

    public LogMessage(LogLevel level, Agents agent, LogMessageType type, String time, String message) {
        this.level = level;
        this.agent = agent;
        this.time = time;
        this.type = type;
        this.message = message;
    }

    public LogLevel getLevel() { return level; }

    public Agents getAgent() { return agent; }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public LogMessageType getType() {
        return type;
    }

    public String getLog(){
        return time + " " + agent + ": [" + level.toString()  + "]" + "<" + type.toString() + "> " + message;
    }
}
