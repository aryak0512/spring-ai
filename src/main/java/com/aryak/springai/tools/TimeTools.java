package com.aryak.springai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class TimeTools {

    @Tool(name = "getCurrentLocalTime", description = "Returns the user's current local time")
    public String getCurrentLocalTime() {
        return LocalDateTime.now().toString();
    }

    @Tool(name = "getCurrentTimeByZone", description = "Get the current time in a particular time zone")
    public String getCurrentTimeByZoneId(@ToolParam(description = "Value representing the time zone") String timeZone) {
        return LocalDateTime.now(ZoneId.of(timeZone))
                .toString();
    }
}
