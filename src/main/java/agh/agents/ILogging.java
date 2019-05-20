package agh.agents;

import agh.utils.Agents;
import agh.utils.LogLevel;
import agh.utils.LogMessage;
import javafx.beans.property.ListProperty;

public interface ILogging {
    ListProperty<LogMessage> getLog();
    void sendMessage(LogLevel level, Agents agent, String message);
}
