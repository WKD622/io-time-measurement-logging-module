package agh.agents;

import agh.utils.LogMessage;
import javafx.beans.property.ListProperty;

public interface ILogging {
    ListProperty<LogMessage> getLog();
}
